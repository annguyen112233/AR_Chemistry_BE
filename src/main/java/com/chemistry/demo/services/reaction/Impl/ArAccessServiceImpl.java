package com.chemistry.demo.services.reaction.Impl;

import com.chemistry.demo.dto.response.reaction.ArAccessResponse;
import com.chemistry.demo.dto.response.reaction.PackageOwnershipResponse;
import com.chemistry.demo.entity.*;
import com.chemistry.demo.enums.AccessSource;
import com.chemistry.demo.enums.AccessStatus;
import com.chemistry.demo.enums.AccessType;
import com.chemistry.demo.enums.PurchaseStatus;
import com.chemistry.demo.repository.*;
import com.chemistry.demo.services.reaction.ArAccessService;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArAccessServiceImpl implements ArAccessService {
    private final UserAccessRepository userAccessRepository;
    private final SecurityUtils securityUtils;
    private final SingleCardPurchaseRepository singleCardPurchaseRepository;
    private final ArPackagePurchaseRepository arPackagePurchaseRepository;
    private final KitActivationCodeRepository kitActivationCodeRepository;
    private final KitItemRepository kitItemRepository;



    public ArAccessResponse getMyArAccess() {
        User user = securityUtils.getCurrentUserCognitoSub();

        Instant now = Instant.now();

        Optional<UserAccess> activeAccessOpt =
                userAccessRepository.findFirstByUserAndStatusAndExpiredAtAfterOrderByExpiredAtDesc(
                        user,
                        AccessStatus.ACTIVE,
                        now
                );

        if (activeAccessOpt.isEmpty()) {
            return ArAccessResponse.builder()
                    .canScanAR(false)
                    .accessType("FREE")
                    .startAt(null)
                    .expiredAt(null)
                    .remainingDays(0)
                    .message("Bạn cần kích hoạt mã kit hoặc mua gói AR 30 Days để quét AR.")
                    .build();
        }

        UserAccess access = activeAccessOpt.get();

        long remainingDays = Duration.between(now, access.getExpiredAt()).toDays();

        return ArAccessResponse.builder()
                .canScanAR(true)
                .accessType(access.getAccessType().name())
                .startAt(access.getStartAt())
                .expiredAt(access.getExpiredAt())
                .remainingDays(Math.max(remainingDays, 0))
                .message("Bạn có thể quét AR.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canScanSubstances(User user, List<ChemicalSubstance> substances) {
        if (user == null || substances == null || substances.isEmpty()) {
            return false;
        }

        Instant now = Instant.now();

        List<UserAccess> activeAccesses =
                userAccessRepository.findByUserAndStatusAndExpiredAtAfter(
                        user,
                        AccessStatus.ACTIVE,
                        now
                );

        if (activeAccesses.isEmpty()) {
            return false;
        }

        Set<String> requiredSubstanceIds = substances.stream()
                .map(ChemicalSubstance::getId)
                .collect(Collectors.toSet());

        Set<String> allowedSubstanceIds = new HashSet<>();

        for (UserAccess access : activeAccesses) {
            if (access.getAccessType() == AccessType.KIT_TRIAL
                    && access.getSource() == AccessSource.KIT_ACTIVATION) {

                addAllowedSubstancesFromKitTrialAccess(
                        user,
                        access,
                        allowedSubstanceIds
                );

                continue;
            }

            if (access.getAccessType() == AccessType.AR_30_DAYS
                    && access.getSource() == AccessSource.AR_PACKAGE_PURCHASE) {

                addAllowedSubstancesFromArPackageAccess(
                        user,
                        access,
                        allowedSubstanceIds
                );

                continue;
            }

            if (access.getAccessType() == AccessType.SINGLE_SUBSTANCE_AR_30_DAYS
                    && access.getSource() == AccessSource.SINGLE_CARD_PURCHASE) {

                addAllowedSubstanceFromSingleCardAccess(
                        user,
                        access,
                        allowedSubstanceIds
                );
            }
        }

        return allowedSubstanceIds.containsAll(requiredSubstanceIds);
    }

    public PackageOwnershipResponse getMyAr30DaysOwnership() {
        User user = securityUtils.getCurrentUserCognitoSub();
        Instant now = Instant.now();

        Optional<UserAccess> accessOpt =
                userAccessRepository.findFirstByUserAndAccessTypeAndStatusAndExpiredAtAfterOrderByExpiredAtDesc(
                        user,
                        AccessType.AR_30_DAYS,
                        AccessStatus.ACTIVE,
                        now
                );

        if (accessOpt.isEmpty()) {
            return PackageOwnershipResponse.builder()
                    .owned(false)
                    .accessType(AccessType.AR_30_DAYS.name())
                    .startAt(null)
                    .expiredAt(null)
                    .remainingDays(0)
                    .message("Bạn chưa sở hữu gói AR 30 Days.")
                    .build();
        }

        UserAccess access = accessOpt.get();

        long remainingSeconds = Duration.between(now, access.getExpiredAt()).getSeconds();
        long remainingDays = (long) Math.ceil(remainingSeconds / 86400.0);

        return PackageOwnershipResponse.builder()
                .owned(true)
                .accessType(access.getAccessType().name())
                .startAt(access.getStartAt())
                .expiredAt(access.getExpiredAt())
                .remainingDays(Math.max(remainingDays, 0))
                .message("Bạn đang sở hữu gói AR 30 Days.")
                .build();
    }

    private void addAllowedSubstancesFromKitTrialAccess(
            User user,
            UserAccess access,
            Set<String> allowedSubstanceIds
    ) {
        if (access.getReferenceId() == null || access.getReferenceId().isBlank()) {
            return;
        }

        kitActivationCodeRepository
                .findByIdAndUsedByUser(access.getReferenceId(), user)
                .ifPresent(activationCode -> {
                    if (activationCode.getKit() == null) {
                        return;
                    }

                    kitItemRepository.findByKit(activationCode.getKit())
                            .forEach(kitSubstance -> {
                                if (kitSubstance.getSubstance() != null) {
                                    allowedSubstanceIds.add(
                                            kitSubstance.getSubstance().getId()
                                    );
                                }
                            });
                });
    }

    private void addAllowedSubstancesFromArPackageAccess(
            User user,
            UserAccess access,
            Set<String> allowedSubstanceIds
    ) {
        if (access.getReferenceId() == null || access.getReferenceId().isBlank()) {
            return;
        }

        arPackagePurchaseRepository
                .findByIdAndUserAndStatus(
                        access.getReferenceId(),
                        user,
                        PurchaseStatus.PAID
                )
                .ifPresent(purchase -> {
                    KitActivationCode activationCode = purchase.getKitActivationCode();

                    if (activationCode == null || activationCode.getKit() == null) {
                        return;
                    }

                    kitItemRepository.findByKit(activationCode.getKit())
                            .forEach(kitSubstance -> {
                                if (kitSubstance.getSubstance() != null) {
                                    allowedSubstanceIds.add(
                                            kitSubstance.getSubstance().getId()
                                    );
                                }
                            });
                });
    }

    private void addAllowedSubstanceFromSingleCardAccess(
            User user,
            UserAccess access,
            Set<String> allowedSubstanceIds
    ) {
        if (access.getReferenceId() == null || access.getReferenceId().isBlank()) {
            return;
        }

        singleCardPurchaseRepository
                .findByIdAndUserAndStatus(
                        access.getReferenceId(),
                        user,
                        PurchaseStatus.PAID
                )
                .ifPresent(purchase -> {
                    if (purchase.getSingleCard() == null
                            || purchase.getSingleCard().getSubstance() == null) {
                        return;
                    }

                    allowedSubstanceIds.add(
                            purchase.getSingleCard().getSubstance().getId()
                    );
                });
    }

}
