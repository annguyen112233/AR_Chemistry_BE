package com.chemistry.demo.services.kit.Impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.kit.AddKitItemRequest;
import com.chemistry.demo.dto.request.kit.CreateFullKitRequest;
import com.chemistry.demo.dto.request.kit.CreateKitRequest;
import com.chemistry.demo.dto.request.kit.UpdateKitRequest;
import com.chemistry.demo.dto.response.kit.KitResponse;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.entity.Kit;
import com.chemistry.demo.entity.KitItem;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.KitErrorCode;
import com.chemistry.demo.exception.KitItemErrorCode;
import com.chemistry.demo.exception.SubstanceErrorCode;
import com.chemistry.demo.mapper.KitMapper;
import com.chemistry.demo.repository.ChemicalSubstanceRepository;
import com.chemistry.demo.repository.KitItemRepository;
import com.chemistry.demo.repository.KitRepository;
import com.chemistry.demo.services.kit.KitService;
import com.chemistry.demo.utils.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KitServiceImpl implements KitService {
    private final KitItemRepository kitItemRepository;
    private final KitRepository kitRepository;
    private final ChemicalSubstanceRepository chemicalSubstanceRepository;

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public KitResponse createKit(CreateKitRequest request) {
        String code = normalizeCode(request.getCode());
        if (kitRepository.existsByCode(code)) {
            throw new AppException(KitErrorCode.KIT_ALREADY_EXISTS, code);
        }

        Kit kit = Kit.builder()
                .code(code)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        Kit savedKit = kitRepository.save(kit);

        if (request.getSubstanceFormulas() != null && !request.getSubstanceFormulas().isEmpty()) {
            for (String formula: request.getSubstanceFormulas()) {
                addSubstanceToKit(savedKit, formula);
            }
        }
        List<KitItem> items = kitItemRepository.findByKitIdAndActiveTrue(savedKit.getId());
        return KitMapper.toResponse(savedKit, items);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public KitResponse createFullKitFromIncludedSubstances(CreateFullKitRequest request) {
        String code = normalizeCode(request.getCode());

        if (kitRepository.existsByCode(code)) {
            throw new RuntimeException("Kit code already exists: " + code);
        }

        Kit kit = Kit.builder()
                .code(code)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .active(true)
                .build();

        Kit savedKit = kitRepository.save(kit);

        List<ChemicalSubstance> substances =
                chemicalSubstanceRepository.findAllByIncludedInFullKitTrueAndActiveTrue();

        for (ChemicalSubstance substance : substances) {
            KitItem kitItem = KitItem.builder()
                    .kit(savedKit)
                    .substance(substance)
                    .quantity(1)
                    .active(true)
                    .build();

            kitItemRepository.save(kitItem);
        }

        List<KitItem> items = kitItemRepository.findByKitIdAndActiveTrue(savedKit.getId());

        return KitMapper.toResponse(savedKit, items);
    }

    @Override
    public PageResponse<KitResponse> getKits(Boolean active, Pageable pageable) {
        Page<Kit> page;

        if (Boolean.TRUE.equals(active)) {
            page = kitRepository.findByActiveTrue(pageable);
        } else {
            page = kitRepository.findAll(pageable);
        }

        return PageResponseUtils.toPageResponse(page, this::toResponseWithItems);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public KitResponse getKitById(String id) {
        Kit kit = kitRepository.findById(id)
                .orElseThrow(() -> new AppException(KitErrorCode.KIT_NOT_FOUND, id));

        return toResponseWithItems(kit);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public KitResponse getKitByCode(String code) {
        String normalizedCode = normalizeCode(code);

        Kit kit = kitRepository.findByCode(normalizedCode)
                .orElseThrow(() -> new AppException(KitErrorCode.KIT_NOT_FOUND, normalizedCode));

        return toResponseWithItems(kit);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public KitResponse updateKit(String id, UpdateKitRequest request) {
        Kit kit = kitRepository.findById(id)
                .orElseThrow(() -> new AppException(KitErrorCode.KIT_NOT_FOUND, id));

        kit.setName(request.getName());
        kit.setDescription(request.getDescription());
        kit.setPrice(request.getPrice());
        kit.setActive(request.getActive() != null ? request.getActive() : kit.getActive());

        Kit saved = kitRepository.save(kit);

        return toResponseWithItems(saved);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public KitResponse addItemToKit(String kitId, AddKitItemRequest request) {
        Kit kit = kitRepository.findById(kitId)
                .orElseThrow(() -> new AppException(KitErrorCode.KIT_NOT_FOUND, kitId));

        addSubstanceToKit(
                kit,
                request.getFormula()
        );

        return toResponseWithItems(kit);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public KitResponse removeItemFromKit(String kitId, String substanceId) {
        Kit kit = kitRepository.findById(kitId)
                .orElseThrow(() -> new AppException(KitErrorCode.KIT_NOT_FOUND, kitId));

        KitItem kitItem = kitItemRepository.findByKitIdAndSubstanceId(kitId, substanceId)
                .orElseThrow(() -> new AppException(KitItemErrorCode.KIT_ITEM_NOT_FOUND));

        kitItem.setActive(false);
        kitItemRepository.save(kitItem);

        return toResponseWithItems(kit);
    }

    @Override
    public KitResponse updateActiveStatus(String id, Boolean active) {
        Kit kit = kitRepository.findById(id)
                .orElseThrow(() -> new AppException(KitErrorCode.KIT_NOT_FOUND, id));

        kit.setActive(active);

        Kit saved = kitRepository.save(kit);

        return toResponseWithItems(saved);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public KitResponse restoreItemToKit(String kitId, String substanceId) {
        Kit kit = kitRepository.findById(kitId)
                .orElseThrow(() -> new AppException(KitErrorCode.KIT_NOT_FOUND, kitId));

        KitItem kitItem = kitItemRepository.findByKitIdAndSubstanceId(kitId, substanceId)
                .orElseThrow(() -> new AppException(KitItemErrorCode.KIT_ITEM_NOT_FOUND));

        kitItem.setActive(true);

        if (kitItem.getQuantity() == null || kitItem.getQuantity() <= 0) {
            kitItem.setQuantity(1);
        }

        kitItemRepository.save(kitItem);

        return toResponseWithItems(kit);
    }

    private void addSubstanceToKit(Kit kit, String formula) {
        String normalizedFormula = normalizeFormula(formula);

        ChemicalSubstance substance = chemicalSubstanceRepository.findByFormula(normalizedFormula)
                .orElseThrow(() -> new AppException(
                        SubstanceErrorCode.SUBSTANCE_NOT_FOUND, formula
                ));

        if (!Boolean.TRUE.equals(substance.getActive())) {
            throw new RuntimeException("Substance is inactive: " + normalizedFormula);
        }

        boolean exists = kitItemRepository.existsByKitIdAndSubstanceId(
                kit.getId(),
                substance.getId()
        );

        if (exists) {
            throw new RuntimeException("Substance already exists in kit: " + normalizedFormula);
        }

        KitItem kitItem = KitItem.builder()
                .kit(kit)
                .substance(substance)
                .quantity(1)
                .active(true)
                .build();

        kitItemRepository.save(kitItem);
    }

    private KitResponse toResponseWithItems(Kit kit) {
        List<KitItem> items = kitItemRepository.findByKitIdAndActiveTrue(kit.getId());

        return KitMapper.toResponse(kit, items);
    }

    private String normalizeCode(String code) {
        if (code == null) {
            throw new RuntimeException("Kit code must not be null");
        }

        return code.trim().toUpperCase();
    }

    private String normalizeFormula(String formula){
        if(formula == null || formula.isBlank()){
            throw new RuntimeException("Formula cannot be null or blank");
        }

        return formula.trim();
    }
}
