package com.chemistry.demo.services.subtance.Impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.subtance.CreateSubstanceRequest;
import com.chemistry.demo.dto.request.subtance.UpdateSubstanceRequest;
import com.chemistry.demo.dto.response.library.LibraryCardResponse;
import com.chemistry.demo.dto.response.substance.SubstanceResponse;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.enums.ChemicalGroup;
import com.chemistry.demo.enums.ChemicalSubstanceType;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.SubstanceErrorCode;
import com.chemistry.demo.mapper.CompoundDetailMapper;
import com.chemistry.demo.mapper.ElementDetailMapper;
import com.chemistry.demo.mapper.SubstanceMapper;
import com.chemistry.demo.repository.ChemicalSubstanceRepository;
import com.chemistry.demo.repository.CompoundDetailRepository;
import com.chemistry.demo.repository.ElementDetailRepository;
import com.chemistry.demo.utils.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubstanceServiceImpl implements com.chemistry.demo.services.subtance.SubstanceService {
    private final ChemicalSubstanceRepository chemicalSubstanceRepository;
    private final ElementDetailRepository elementDetailRepository;
    private final CompoundDetailRepository compoundDetailRepository;
    @Transactional
    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public SubstanceResponse createSubstance(CreateSubstanceRequest request) {
        String formula = normalizeFormula(request.getFormula());

        if (chemicalSubstanceRepository.existsByFormula(formula)) {
            throw new AppException(SubstanceErrorCode.SUBSTANCE_ALREADY_EXISTS);
        }

        ChemicalSubstance substance = ChemicalSubstance.builder()
                .formula(formula)
                .name(request.getName())
                .vietnameseName(request.getVietnameseName())
                .type(request.getType())
                .chemicalGroup(request.getChemicalGroup())
                .state(request.getState())
                .molarMass(request.getMolarMass())
                .active(request.getActive() != null ? request.getActive() : true)
                .includedInFullKit(
                        request.getIncludedInFullKit() != null
                                ? request.getIncludedInFullKit()
                                : false
                )
                .description(request.getDescription())
                .safetyNote(request.getSafetyNote())
                .build();

        ChemicalSubstance saved = chemicalSubstanceRepository.save(substance);

        return toResponseWithDetails(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageResponse<SubstanceResponse> getSubstances(
            Boolean active,
            ChemicalGroup chemicalGroup,
            ChemicalSubstanceType type,
            Boolean includedInFullKit,
            Pageable pageable
    ) {
        Page<ChemicalSubstance> page;

        if (Boolean.TRUE.equals(active)
                && Boolean.TRUE.equals(includedInFullKit)
                && type != null) {
            page = chemicalSubstanceRepository.findByTypeAndIncludedInFullKitTrue(
                    type,
                    pageable
            );

        } else if (Boolean.TRUE.equals(active)
                && Boolean.FALSE.equals(includedInFullKit)
                && type != null) {
            page = chemicalSubstanceRepository.findByTypeAndIncludedInFullKitFalse(
                    type,
                    pageable
            );

        } else if (Boolean.TRUE.equals(active) && chemicalGroup != null) {
            page = chemicalSubstanceRepository.findByChemicalGroupAndActiveTrue(
                    chemicalGroup,
                    pageable
            );

        } else if (Boolean.TRUE.equals(active) && type != null) {
            page = chemicalSubstanceRepository.findByTypeAndActiveTrue(
                    type,
                    pageable
            );

        } else if (Boolean.TRUE.equals(active)
                && Boolean.TRUE.equals(includedInFullKit)) {
            page = chemicalSubstanceRepository.findByIncludedInFullKitTrueAndActiveTrue(
                    pageable
            );

        } else if (Boolean.TRUE.equals(active)) {
            page = chemicalSubstanceRepository.findByActiveTrue(pageable);

        } else if (Boolean.FALSE.equals(active)) {
            page = chemicalSubstanceRepository.findByActiveFalse(pageable);

        } else if (chemicalGroup != null) {
            page = chemicalSubstanceRepository.findByChemicalGroup(
                    chemicalGroup,
                    pageable
            );

        } else if (type != null) {
            page = chemicalSubstanceRepository.findByType(
                    type,
                    pageable
            );

        } else if (Boolean.TRUE.equals(includedInFullKit)) {
            page = chemicalSubstanceRepository.findByIncludedInFullKitTrue(pageable);

        } else if (Boolean.FALSE.equals(includedInFullKit)) {
            page = chemicalSubstanceRepository.findByIncludedInFullKitFalse(pageable);

        } else {
            page = chemicalSubstanceRepository.findAll(pageable);
        }

        return PageResponseUtils.toPageResponse(page, this::toResponseWithDetails);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public SubstanceResponse getSubstanceById(String id) {
        ChemicalSubstance substance = chemicalSubstanceRepository.findById(id)
                .orElseThrow(() -> new AppException(SubstanceErrorCode.SUBSTANCE_NOT_FOUND));

        return toResponseWithDetails(substance);
    }


    @Transactional(readOnly = true)
    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public SubstanceResponse getSubstanceByFormula(String formula) {
        String normalizedFormula = normalizeFormula(formula);

        ChemicalSubstance substance = chemicalSubstanceRepository.findByFormula(normalizedFormula)
                .orElseThrow(() -> new AppException(SubstanceErrorCode.SUBSTANCE_NOT_FOUND));

        return toResponseWithDetails(substance);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public SubstanceResponse updateSubstance(String id, UpdateSubstanceRequest request) {
        ChemicalSubstance substance = chemicalSubstanceRepository.findById(id)
                .orElseThrow(() -> new AppException(SubstanceErrorCode.SUBSTANCE_NOT_FOUND));

        substance.setName(request.getName());
        substance.setVietnameseName(request.getVietnameseName());
        substance.setType(request.getType());
        substance.setChemicalGroup(request.getChemicalGroup());
        substance.setState(request.getState());
        substance.setMolarMass(request.getMolarMass());
        substance.setActive(request.getActive() != null ? request.getActive() : substance.getActive());
        substance.setIncludedInFullKit(
                request.getIncludedInFullKit() != null
                        ? request.getIncludedInFullKit()
                        : substance.getIncludedInFullKit()
        );
        substance.setDescription(request.getDescription());
        substance.setSafetyNote(request.getSafetyNote());

        ChemicalSubstance saved = chemicalSubstanceRepository.save(substance);

        return toResponseWithDetails(saved);
    }

    @Transactional
    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public SubstanceResponse updateActiveStatus(String id, Boolean active) {
        ChemicalSubstance substance = chemicalSubstanceRepository.findById(id)
                .orElseThrow(() -> new AppException(SubstanceErrorCode.SUBSTANCE_NOT_FOUND));

        substance.setActive(active);

        ChemicalSubstance saved = chemicalSubstanceRepository.save(substance);

        return toResponseWithDetails(saved);
    }

    @Transactional
    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public SubstanceResponse updateIncludedInFullKit(String id, Boolean includedInFullKit) {
        ChemicalSubstance substance = chemicalSubstanceRepository.findById(id)
                .orElseThrow(() -> new AppException(SubstanceErrorCode.SUBSTANCE_NOT_FOUND));

        substance.setIncludedInFullKit(includedInFullKit);

        ChemicalSubstance saved = chemicalSubstanceRepository.save(substance);

        return toResponseWithDetails(saved);
    }




    private String normalizeFormula(String formula) {
        if (formula == null) {
            throw new RuntimeException("Formula must not be null");
        }

        return formula.trim();
    }

    private SubstanceResponse toResponseWithDetails(ChemicalSubstance substance) {
        SubstanceResponse response = SubstanceMapper.toResponse(substance);

        elementDetailRepository.findBySubstance_Id(substance.getId())
                .map(ElementDetailMapper::toResponse)
                .ifPresent(response::setElementDetail);

        compoundDetailRepository.findBySubstance_Id(substance.getId())
                .map(CompoundDetailMapper::toResponse)
                .ifPresent(response::setCompoundDetail);

        return response;
    }
}
