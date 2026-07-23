package com.chemistry.demo.services.reaction.Impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.reaction.CreateReactionRequest;
import com.chemistry.demo.dto.request.reaction.ReactionSubstanceRequest;
import com.chemistry.demo.dto.request.reaction.UpdateReactionRequest;
import com.chemistry.demo.dto.response.reaction.ReactionResponse;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.entity.ReactionDefinition;
import com.chemistry.demo.entity.ReactionSubstance;
import com.chemistry.demo.enums.ArSceneKey;
import com.chemistry.demo.enums.ReactionCategory;
import com.chemistry.demo.enums.ReactionRole;
import com.chemistry.demo.enums.ReactionType;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.ReactionErrorCode;
import com.chemistry.demo.exception.SubstanceErrorCode;
import com.chemistry.demo.mapper.ReactionMapper;
import com.chemistry.demo.repository.ChemicalSubstanceRepository;
import com.chemistry.demo.repository.ReactionDefinitionRepository;
import com.chemistry.demo.repository.ReactionSubstanceRepository;
import com.chemistry.demo.services.reaction.ReactionService;
import com.chemistry.demo.utils.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactionServiceImpl
        implements ReactionService {

    private final ReactionDefinitionRepository
            reactionDefinitionRepository;

    private final ReactionSubstanceRepository
            reactionSubstanceRepository;

    private final ChemicalSubstanceRepository
            chemicalSubstanceRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ReactionResponse createReaction(
            CreateReactionRequest request
    ) {
        validateCreateRequest(request);

        String code =
                normalizeCode(request.getCode());

        if (reactionDefinitionRepository
                .existsByCode(code)) {
            throw new AppException(
                    ReactionErrorCode.REACTION_ALREADY_EXISTS,
                    code
            );
        }

        ReactionDefinition reaction =
                ReactionDefinition.builder()
                        .code(code)
                        .name(
                                normalizeRequiredText(
                                        request.getName(),
                                        "Reaction name"
                                )
                        )
                        .equation(
                                normalizeRequiredText(
                                        request.getEquation(),
                                        "Reaction equation"
                                )
                        )
                        .reactionCategory(
                                request.getReactionCategory()
                        )
                        .reactionType(
                                request.getReactionType()
                        )
                        .arSceneKey(
                                request.getArSceneKey()
                        )
                        .description(
                                normalizeNullableText(
                                        request.getDescription()
                                )
                        )
                        .script(
                                normalizeNullableText(
                                        request.getScript()
                                )
                        )
                        .grade(request.getGrade())
                        .active(
                                request.getActive() != null
                                        ? request.getActive()
                                        : true
                        )
                        .build();

        ReactionDefinition savedReaction =
                reactionDefinitionRepository.save(
                        reaction
                );

        List<ReactionSubstance> reactants =
                createReactionSubstances(
                        savedReaction,
                        request.getReactants(),
                        ReactionRole.REACTANT
                );

        List<ReactionSubstance> products =
                createReactionSubstances(
                        savedReaction,
                        request.getProducts(),
                        ReactionRole.PRODUCT
                );

        return ReactionMapper.toResponse(
                savedReaction,
                reactants,
                products
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReactionResponse> getReactions(
            Boolean active,
            ReactionType reactionType,
            ArSceneKey arSceneKey,
            ReactionCategory reactionCategory,
            Integer grade,
            Pageable pageable
    ) {
        Page<ReactionDefinition> page;

        if (grade != null && reactionCategory != null) {
            page = reactionDefinitionRepository
                    .findByGradeAndReactionCategory(
                            grade,
                            reactionCategory,
                            pageable
                    );

        } else if (reactionCategory != null) {
            page = reactionDefinitionRepository
                    .findByReactionCategory(
                            reactionCategory,
                            pageable
                    );

        } else if (grade != null) {
            page = reactionDefinitionRepository
                    .findByGrade(
                            grade,
                            pageable
                    );

        } else if (Boolean.TRUE.equals(active)
                && reactionType != null) {

            page = reactionDefinitionRepository
                    .findByReactionTypeAndActiveTrue(
                            reactionType,
                            pageable
                    );

        } else if (Boolean.TRUE.equals(active)
                && arSceneKey != null) {

            page = reactionDefinitionRepository
                    .findByArSceneKeyAndActiveTrue(
                            arSceneKey,
                            pageable
                    );

        } else if (Boolean.TRUE.equals(active)) {
            page = reactionDefinitionRepository
                    .findByActiveTrue(pageable);

        } else {
            page = reactionDefinitionRepository
                    .findAll(pageable);
        }

        return PageResponseUtils.toPageResponse(
                page,
                this::toResponseWithSubstances
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ReactionResponse getReactionById(
            String id
    ) {
        ReactionDefinition reaction =
                reactionDefinitionRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new AppException(
                                        ReactionErrorCode.REACTION_NOT_FOUND,
                                        id
                                )
                        );

        return toResponseWithSubstances(
                reaction
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ReactionResponse getReactionByCode(
            String code
    ) {
        String normalizedCode =
                normalizeCode(code);

        ReactionDefinition reaction =
                reactionDefinitionRepository
                        .findByCode(normalizedCode)
                        .orElseThrow(() ->
                                new AppException(
                                        ReactionErrorCode.REACTION_NOT_FOUND,
                                        normalizedCode
                                )
                        );

        return toResponseWithSubstances(
                reaction
        );
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ReactionResponse updateReaction(
            String id,
            UpdateReactionRequest request
    ) {
        validateUpdateRequest(request);

        ReactionDefinition reaction =
                reactionDefinitionRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new AppException(
                                        ReactionErrorCode.REACTION_NOT_FOUND,
                                        id
                                )
                        );

        reaction.setName(
                normalizeRequiredText(
                        request.getName(),
                        "Reaction name"
                )
        );

        reaction.setEquation(
                normalizeRequiredText(
                        request.getEquation(),
                        "Reaction equation"
                )
        );

        reaction.setReactionCategory(
                request.getReactionCategory()
        );

        reaction.setReactionType(
                request.getReactionType()
        );

        reaction.setArSceneKey(
                request.getArSceneKey()
        );

        reaction.setDescription(
                normalizeNullableText(
                        request.getDescription()
                )
        );

        reaction.setScript(
                normalizeNullableText(
                        request.getScript()
                )
        );

        reaction.setGrade(
                request.getGrade()
        );

        if (request.getActive() != null) {
            reaction.setActive(
                    request.getActive()
            );
        }

        ReactionDefinition savedReaction =
                reactionDefinitionRepository.save(
                        reaction
                );

        List<ReactionSubstance> oldSubstances =
                reactionSubstanceRepository
                        .findByReactionId(
                                savedReaction.getId()
                        );

        if (!oldSubstances.isEmpty()) {
            reactionSubstanceRepository
                    .deleteAll(oldSubstances);

            reactionSubstanceRepository.flush();
        }

        List<ReactionSubstance> reactants =
                createReactionSubstances(
                        savedReaction,
                        request.getReactants(),
                        ReactionRole.REACTANT
                );

        List<ReactionSubstance> products =
                createReactionSubstances(
                        savedReaction,
                        request.getProducts(),
                        ReactionRole.PRODUCT
                );

        return ReactionMapper.toResponse(
                savedReaction,
                reactants,
                products
        );
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ReactionResponse updateActiveStatus(
            String id,
            Boolean active
    ) {
        if (active == null) {
            throw new IllegalArgumentException(
                    "active is required"
            );
        }

        ReactionDefinition reaction =
                reactionDefinitionRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new AppException(
                                        ReactionErrorCode.REACTION_NOT_FOUND,
                                        id
                                )
                        );

        reaction.setActive(active);

        ReactionDefinition saved =
                reactionDefinitionRepository.save(
                        reaction
                );

        return toResponseWithSubstances(
                saved
        );
    }

    private List<ReactionSubstance>
    createReactionSubstances(
            ReactionDefinition reaction,
            List<ReactionSubstanceRequest> requests,
            ReactionRole role
    ) {
        if (requests == null
                || requests.isEmpty()) {
            return Collections.emptyList();
        }

        List<ReactionSubstance> result =
                new ArrayList<>();

        int substanceOrder = 1;

        for (ReactionSubstanceRequest item
                : requests) {

            String formula =
                    normalizeFormula(
                            item.getFormula()
                    );

            ChemicalSubstance substance =
                    chemicalSubstanceRepository
                            .findByFormula(formula)
                            .orElseThrow(() ->
                                    new AppException(
                                            SubstanceErrorCode.SUBSTANCE_NOT_FOUND,
                                            formula
                                    )
                            );

            if (!Boolean.TRUE.equals(
                    substance.getActive()
            )) {
                throw new IllegalStateException(
                        "Substance is inactive: "
                                + formula
                );
            }

            int coefficient =
                    item.getCoefficient() != null
                            ? item.getCoefficient()
                            : 1;

            if (coefficient <= 0) {
                throw new IllegalArgumentException(
                        "Coefficient must be greater than 0: "
                                + formula
                );
            }

            ReactionSubstance reactionSubstance =
                    ReactionSubstance.builder()
                            .reaction(reaction)
                            .substance(substance)
                            .role(role)
                            .coefficient(coefficient)
                            .substanceOrder(
                                    substanceOrder++
                            )
                            .build();

            result.add(reactionSubstance);
        }

        return reactionSubstanceRepository
                .saveAll(result);
    }

    private ReactionResponse toResponseWithSubstances(
            ReactionDefinition reaction
    ) {
        List<ReactionSubstance> reactants =
                reactionSubstanceRepository
                        .findByReactionAndRoleOrderBySubstanceOrderAsc(
                                reaction,
                                ReactionRole.REACTANT
                        );

        List<ReactionSubstance> products =
                reactionSubstanceRepository
                        .findByReactionAndRoleOrderBySubstanceOrderAsc(
                                reaction,
                                ReactionRole.PRODUCT
                        );

        return ReactionMapper.toResponse(
                reaction,
                reactants,
                products
        );
    }

    private void validateCreateRequest(
            CreateReactionRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Request is required"
            );
        }

        validateReactionConfiguration(
                request.getGrade(),
                request.getReactionCategory(),
                request.getReactionType(),
                request.getArSceneKey(),
                request.getReactants()
        );
    }

    private void validateUpdateRequest(
            UpdateReactionRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Request is required"
            );
        }

        validateReactionConfiguration(
                request.getGrade(),
                request.getReactionCategory(),
                request.getReactionType(),
                request.getArSceneKey(),
                request.getReactants()
        );
    }

    private void validateReactionConfiguration(
            Integer grade,
            ReactionCategory reactionCategory,
            ReactionType reactionType,
            ArSceneKey arSceneKey,
            List<ReactionSubstanceRequest> reactants
    ) {
        if (grade == null
                || grade < 8
                || grade > 12) {
            throw new IllegalArgumentException(
                    "Grade must be from 8 to 12"
            );
        }

        if (reactionCategory == null) {
            throw new IllegalArgumentException(
                    "reactionCategory is required"
            );
        }

        if (reactionType == null) {
            throw new IllegalArgumentException(
                    "reactionType is required"
            );
        }

        if (arSceneKey == null) {
            throw new IllegalArgumentException(
                    "arSceneKey is required"
            );
        }

        if (reactants == null
                || reactants.isEmpty()
                || reactants.size() > 2) {
            throw new IllegalArgumentException(
                    "Reaction must contain one or two reactants"
            );
        }
    }

    private String normalizeCode(
            String code
    ) {
        if (code == null
                || code.isBlank()) {
            throw new IllegalArgumentException(
                    "Reaction code must not be blank"
            );
        }

        return code
                .trim()
                .toUpperCase();
    }

    private String normalizeFormula(
            String formula
    ) {
        if (formula == null
                || formula.isBlank()) {
            throw new IllegalArgumentException(
                    "Formula must not be blank"
            );
        }

        return formula.trim();
    }

    private String normalizeRequiredText(
            String value,
            String fieldName
    ) {
        if (value == null
                || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must not be blank"
            );
        }

        return value.trim();
    }

    private String normalizeNullableText(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }
}