package com.chemistry.demo.services.inventory.Impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.KitActivationCode.ActivateKitRequest;
import com.chemistry.demo.dto.response.KitActivationCode.ActivateKitResponse;
import com.chemistry.demo.dto.response.compoundDetail.CompoundDetailResponse;
import com.chemistry.demo.dto.response.elementDetail.ElementDetailResponse;
import com.chemistry.demo.dto.response.inventory.UserInventoryResponse;
import com.chemistry.demo.dto.response.substance.SubstanceDetailResponse;
import com.chemistry.demo.entity.*;
import com.chemistry.demo.enums.*;
import com.chemistry.demo.exception.ActivationCodeErrorCode;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.mapper.InventoryMapper;
import com.chemistry.demo.repository.*;
import com.chemistry.demo.services.inventory.InventoryService;
import com.chemistry.demo.utils.PageResponseUtils;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final KitActivationCodeRepository kitActivationCodeRepository;
    private final KitItemRepository kitItemRepository;
    private final UserAccessRepository userAccessRepository;
    private final ElementDetailRepository elementDetailRepository;
    private final UserInventoryRepository userInventoryRepository;
    private final SecurityUtils securityUtils;
    private final CompoundDetailRepository compoundDetailRepository;

    @Transactional
    public ActivateKitResponse activateKit(
            ActivateKitRequest request
    ) {
        User user = securityUtils.getCurrentUserCognitoSub();
        String code = normalizeCode(request.getActivationCode());

        KitActivationCode activationCode = kitActivationCodeRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ActivationCodeErrorCode.ACTIVATION_CODE_NOT_FOUND, code));

        validateActivationCode(activationCode);

        Kit kit = activationCode.getKit();

        if (kit == null) {
            throw new RuntimeException("Activation code does not belong to any kit");
        }

        if (!Boolean.TRUE.equals(kit.getActive())) {
            throw new RuntimeException("Kit is inactive: " + kit.getCode());
        }

        List<KitItem> kitItems = kitItemRepository.findByKitIdAndActiveTrue(kit.getId());

        if (kitItems.isEmpty()) {
            throw new RuntimeException("Kit has no active items: " + kit.getCode());
        }

        Instant now = Instant.now();
        Instant expiredAt = now.plus(30, ChronoUnit.DAYS);

        List<ChemicalSubstance> unlockedSubstances = new ArrayList<>();

        for (KitItem kitItem : kitItems) {
            ChemicalSubstance substance = kitItem.getSubstance();

            if (substance == null || !Boolean.TRUE.equals(substance.getActive())) {
                continue;
            }

            UserInventory inventory = userInventoryRepository
                    .findByUserAndSubstance(user, substance)
                    .orElse(null);

            if (inventory == null) {
                inventory = UserInventory.builder()
                        .user(user)
                        .substance(substance)
                        .source(InventorySource.KIT_ACTIVATION)
                        .sourceRef(activationCode.getCode())
                        .quantity(
                                kitItem.getQuantity() != null
                                        ? kitItem.getQuantity()
                                        : 1
                        )
                        .active(true)
                        .acquiredAt(now)
                        .expiresAt(null)
                        .build();

                userInventoryRepository.save(inventory);
            } else {
                inventory.setActive(true);

                int currentQuantity = inventory.getQuantity() != null
                        ? inventory.getQuantity()
                        : 0;

                int addedQuantity = kitItem.getQuantity() != null
                        ? kitItem.getQuantity()
                        : 1;

                inventory.setQuantity(currentQuantity + addedQuantity);
                inventory.setSource(InventorySource.KIT_ACTIVATION);
                inventory.setSourceRef(activationCode.getCode());

                if (inventory.getAcquiredAt() == null) {
                    inventory.setAcquiredAt(now);
                }

                userInventoryRepository.save(inventory);
            }

            unlockedSubstances.add(substance);
        }

        userAccessRepository.save(
                UserAccess.builder()
                        .user(user)
                        .accessType(AccessType.KIT_TRIAL)
                        .source(AccessSource.KIT_ACTIVATION)
                        .startAt(now)
                        .expiredAt(expiredAt)
                        .status(AccessStatus.ACTIVE)
                        .referenceId(activationCode.getId())
                        .build()
        );

        activationCode.setStatus(ActivationCodeStatus.USED);
        activationCode.setUsedByUser(user);
        activationCode.setUsedAt(now);
        activationCode.setActive(false);

        kitActivationCodeRepository.save(activationCode);

        return ActivateKitResponse.builder()
                .success(true)
                .message("Kích hoạt bộ kit thành công. Bạn có thể quét AR trong 30 ngày.")
                .kitId(kit.getId())
                .kitCode(kit.getCode())
                .kitName(kit.getName())
                .activationCode(activationCode.getCode())
                .unlockedSubstances(
                        unlockedSubstances.stream()
                                .map(InventoryMapper::toUnlockedSubstance)
                                .toList()
                )
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<UserInventoryResponse> getMyInventory(
            Pageable pageable
    ) {
        User user = securityUtils.getCurrentUserCognitoSub();
        Page<UserInventory> page =
                userInventoryRepository.findByUserAndActiveTrue(user, pageable);

        return PageResponseUtils.toPageResponse(page, InventoryMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SubstanceDetailResponse getSubstanceDetail(String substanceId) {
        User user = securityUtils.getCurrentUserCognitoSub();

        UserInventory inventory = userInventoryRepository.findByUserAndSubstance_Id(user, substanceId)
                .orElseThrow(() -> new RuntimeException("You do not own this substance"));

        ChemicalSubstance substance = inventory.getSubstance();

        ElementDetail elementDetail = elementDetailRepository.findBySubstance_Id(substance.getId())
                .orElse(null);

        CompoundDetail compoundDetail = compoundDetailRepository.findBySubstance_Id(substance.getId())
                .orElse(null);

        return SubstanceDetailResponse.builder()
                .substanceId(substance.getId())
                .formula(substance.getFormula())
                .name(substance.getName())
                .vietnameseName(substance.getVietnameseName())
                .chemicalGroup(substance.getChemicalGroup())
                .state(substance.getState())
                .elementDetail(
                        elementDetail == null
                                ? null
                                : ElementDetailResponse.builder()
                                .atomicNumber(elementDetail.getAtomicNumber())
                                .symbol(elementDetail.getSymbol())
                                .periodicCategory(elementDetail.getPeriodicCategory())
                                .atomicMass(elementDetail.getAtomicMass())
                                .period(elementDetail.getPeriod())
                                .groupNumber(elementDetail.getGroupNumber())
                                .build()
                )
                .compoundDetail(
                        compoundDetail == null
                                ? null
                                : CompoundDetailResponse.builder()
                                .iupacName(compoundDetail.getIupacName())
                                .casNumber(compoundDetail.getCasNumber())
                                .compoundClass(compoundDetail.getCompoundClass())
                                .physicalInKit(compoundDetail.getPhysicalInKit())
                                .reactionProductOnly(compoundDetail.getReactionProductOnly())
                                .usageNote(compoundDetail.getUsageNote())
                                .build()
                )
                .build();
    }

    private void validateActivationCode(KitActivationCode activationCode) {
        if (!Boolean.TRUE.equals(activationCode.getActive())) {
            throw new AppException(ActivationCodeErrorCode.ACTIVATION_CODE_INACTIVE);
        }

        if (activationCode.getStatus() == ActivationCodeStatus.USED) {
            throw new AppException(ActivationCodeErrorCode.ACTIVATION_CODE_ALREADY_USED);
        }

        if (activationCode.getStatus() == ActivationCodeStatus.LOCKED) {
            throw new AppException(ActivationCodeErrorCode.ACTIVATION_CODE_IS_LOCKED);
        }

        if (activationCode.getStatus() == ActivationCodeStatus.EXPIRED) {
            throw new AppException(ActivationCodeErrorCode.ACTIVATION_CODE_IS_EXPIRED);
        }

        if (activationCode.getStatus() != ActivationCodeStatus.UNUSED) {
            throw new AppException(ActivationCodeErrorCode.ACTIVATION_CODE_NOT_AVAILABLE);
        }

        if (activationCode.getExpiresAt() != null
                && activationCode.getExpiresAt().isBefore(Instant.now())) {
            activationCode.setStatus(ActivationCodeStatus.EXPIRED);
            activationCode.setActive(false);
            kitActivationCodeRepository.save(activationCode);

            throw new AppException(ActivationCodeErrorCode.ACTIVATION_CODE_IS_EXPIRED);
        }
    }

    private String normalizeCode(String code) {
        if (code == null) {
            throw new RuntimeException("Activation code must not be null");
        }

        return code.trim().toUpperCase();
    }
}
