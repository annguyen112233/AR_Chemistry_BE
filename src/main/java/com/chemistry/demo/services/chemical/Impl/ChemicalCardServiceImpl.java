package com.chemistry.demo.services.chemical.Impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.chemical.CreateChemicalCardRequest;
import com.chemistry.demo.dto.request.chemical.UpdateChemicalCardRequest;
import com.chemistry.demo.dto.response.chemical.ChemicalCardResponse;
import com.chemistry.demo.entity.ChemicalCard;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.mapper.ChemicalCardMapper;
import com.chemistry.demo.repository.ChemicalCardRepository;
import com.chemistry.demo.repository.ChemicalSubstanceRepository;
import com.chemistry.demo.services.chemical.ChemicalCardService;
import com.chemistry.demo.utils.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChemicalCardServiceImpl implements ChemicalCardService {
    private final ChemicalCardRepository chemicalCardRepository;
    private final ChemicalSubstanceRepository chemicalSubstanceRepository;

    @Transactional
    @Override
    public ChemicalCardResponse createCard(CreateChemicalCardRequest request) {
        String cardCode = normalizeCode(request.getCardCode());
        String qrPayload = normalizeQrPayload(request.getQrPayload());
        String formula = normalizeFormula(request.getFormula());

        if (chemicalCardRepository.existsByCardCode(cardCode)) {
            throw new RuntimeException("Card code already exists: " + cardCode);
        }

        if (chemicalCardRepository.existsByQrPayload(qrPayload)) {
            throw new RuntimeException("QR payload already exists: " + qrPayload);
        }

        ChemicalSubstance substance = chemicalSubstanceRepository.findByFormula(formula)
                .orElseThrow(() -> new RuntimeException("Substance not found: " + formula));

        if (!Boolean.TRUE.equals(substance.getActive())) {
            throw new RuntimeException("Substance is inactive: " + formula);
        }

        ChemicalCard card = ChemicalCard.builder()
                .cardCode(cardCode)
                .qrPayload(qrPayload)
                .substance(substance)
                .displayName(
                        request.getDisplayName() != null
                                ? request.getDisplayName()
                                : buildDefaultDisplayName(substance)
                )
                .imageUrl(request.getImageUrl())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        ChemicalCard saved = chemicalCardRepository.save(card);

        return ChemicalCardMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<ChemicalCardResponse> getCards(
            Boolean active,
            String substanceId,
            Pageable pageable
    ) {
        Page<ChemicalCard> page;

        if (Boolean.TRUE.equals(active)) {
            page = chemicalCardRepository.findByActiveTrue(pageable);
        } else if (substanceId != null && !substanceId.isBlank()) {
            page = chemicalCardRepository.findBySubstance_Id(substanceId, pageable);
        } else {
            page = chemicalCardRepository.findAll(pageable);
        }

        return PageResponseUtils.toPageResponse(page, ChemicalCardMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public ChemicalCardResponse getById(String id) {
        ChemicalCard card = chemicalCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chemical card not found: " + id));

        return ChemicalCardMapper.toResponse(card);
    }

    @Transactional(readOnly = true)
    @Override
    public ChemicalCardResponse getByCardCode(String cardCode) {
        String normalized = normalizeCode(cardCode);

        ChemicalCard card = chemicalCardRepository.findByCardCode(normalized)
                .orElseThrow(() -> new RuntimeException("Chemical card not found: " + normalized));

        return ChemicalCardMapper.toResponse(card);
    }

    @Transactional(readOnly = true)
    @Override
    public ChemicalCardResponse getByQrPayload(String qrPayload) {
        String normalized = normalizeQrPayload(qrPayload);

        ChemicalCard card = chemicalCardRepository.findByQrPayload(normalized)
                .orElseThrow(() -> new RuntimeException("Chemical card not found: " + normalized));

        return ChemicalCardMapper.toResponse(card);
    }

    @Transactional
    @Override
    public ChemicalCardResponse updateCard(String id, UpdateChemicalCardRequest request) {
        ChemicalCard card = chemicalCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chemical card not found: " + id));

        String qrPayload = normalizeQrPayload(request.getQrPayload());
        String formula = normalizeFormula(request.getFormula());

        chemicalCardRepository.findByQrPayload(qrPayload)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(card.getId())) {
                        throw new RuntimeException("QR payload already exists: " + qrPayload);
                    }
                });

        ChemicalSubstance substance = chemicalSubstanceRepository.findByFormula(formula)
                .orElseThrow(() -> new RuntimeException("Substance not found: " + formula));

        card.setQrPayload(qrPayload);
        card.setSubstance(substance);
        card.setDisplayName(request.getDisplayName());
        card.setImageUrl(request.getImageUrl());
        card.setActive(request.getActive() != null ? request.getActive() : card.getActive());

        ChemicalCard saved = chemicalCardRepository.save(card);

        return ChemicalCardMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public ChemicalCardResponse updateActiveStatus(String id, Boolean active) {
        ChemicalCard card = chemicalCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chemical card not found: " + id));

        card.setActive(active);

        ChemicalCard saved = chemicalCardRepository.save(card);

        return ChemicalCardMapper.toResponse(saved);
    }

    private String buildDefaultDisplayName(ChemicalSubstance substance) {
        if (substance.getVietnameseName() != null && !substance.getVietnameseName().isBlank()) {
            return substance.getFormula() + " - " + substance.getVietnameseName();
        }

        return substance.getFormula() + " - " + substance.getName();
    }

    private String normalizeCode(String value) {
        if (value == null) {
            throw new RuntimeException("Card code must not be null");
        }
        return value.trim().toUpperCase();
    }

    private String normalizeQrPayload(String value) {
        if (value == null) {
            throw new RuntimeException("QR payload must not be null");
        }
        return value.trim().toUpperCase();
    }

    private String normalizeFormula(String value) {
        if (value == null) {
            throw new RuntimeException("Formula must not be null");
        }
        return value.trim();
    }
}
