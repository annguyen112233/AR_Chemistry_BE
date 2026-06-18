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
import java.util.List;
@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {
    private final ReactionDefinitionRepository reactionDefinitionRepository;
    private final ReactionSubstanceRepository reactionSubstanceRepository;
    private final ChemicalSubstanceRepository chemicalSubstanceRepository;
    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ReactionResponse createReaction(CreateReactionRequest request) {
        String code = normalizeCode(request.getCode());
        if (reactionDefinitionRepository.existsByCode(code)) {
            throw new AppException(ReactionErrorCode.REACTION_ALREADY_EXISTS, code);
        }

        ReactionDefinition reactionDefinition = ReactionDefinition.builder()
                .code(code)
                .name(request.getName())
                .equation(request.getEquation())
                .reactionType(request.getReactionType())
                .arSceneKey(request.getArSceneKey())
                .description(request.getDescription())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        ReactionDefinition savedReaction = reactionDefinitionRepository.save(reactionDefinition);

        List<ReactionSubstance> reactants = createReactionSubstances(
                savedReaction,
                request.getReactants(),
                ReactionRole.REACTANT
        );

        List<ReactionSubstance> products = createReactionSubstances(
                savedReaction,
                request.getProducts(),
                ReactionRole.PRODUCT
        );

        return ReactionMapper.toResponse(savedReaction, reactants, products);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReactionResponse> getReactions(Boolean active, ReactionType reactionType, ArSceneKey arSceneKey, Pageable pageable) {
        Page<ReactionDefinition> page;

        if (Boolean.TRUE.equals(active) && reactionType != null) {
            page = reactionDefinitionRepository.findByReactionTypeAndActiveTrue(
                    reactionType,
                    pageable
            );
        } else if (Boolean.TRUE.equals(active) && arSceneKey != null) {
            page = reactionDefinitionRepository.findByArSceneKeyAndActiveTrue(
                    arSceneKey,
                    pageable
            );
        } else if (Boolean.TRUE.equals(active)) {
            page = reactionDefinitionRepository.findByActiveTrue(pageable);
        } else {
            page = reactionDefinitionRepository.findAll(pageable);
        }

        return PageResponseUtils.toPageResponse(page, this::toResponseWithSubstances);
    }

    @Override
    @Transactional(readOnly = true)
    public ReactionResponse getReactionById(String id) {
        ReactionDefinition reaction = reactionDefinitionRepository.findById(id)
                .orElseThrow(() -> new AppException(ReactionErrorCode.REACTION_NOT_FOUND, id));

        return toResponseWithSubstances(reaction);
    }

    @Override
    @Transactional(readOnly = true)
    public ReactionResponse getReactionByCode(String code) {
        String normalizedCode = normalizeCode(code);

        ReactionDefinition reaction = reactionDefinitionRepository.findByCode(normalizedCode)
                .orElseThrow(() -> new AppException(
                        ReactionErrorCode.REACTION_NOT_FOUND, normalizedCode
                ));

        return toResponseWithSubstances(reaction);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ReactionResponse updateReaction(String id, UpdateReactionRequest request) {
        ReactionDefinition reaction = reactionDefinitionRepository.findById(id)
                .orElseThrow(() -> new AppException(ReactionErrorCode.REACTION_NOT_FOUND, id));

        reaction.setName(request.getName());
        reaction.setEquation(request.getEquation());
        reaction.setReactionType(request.getReactionType());
        reaction.setArSceneKey(request.getArSceneKey());
        reaction.setDescription(request.getDescription());
        reaction.setActive(request.getActive() != null ? request.getActive() : reaction.getActive());

        ReactionDefinition savedReaction = reactionDefinitionRepository.save(reaction);

        List<ReactionSubstance> oldSubstances =
                reactionSubstanceRepository.findByReactionId(savedReaction.getId());

        reactionSubstanceRepository.deleteAll(oldSubstances);

        List<ReactionSubstance> reactants = createReactionSubstances(
                savedReaction,
                request.getReactants(),
                ReactionRole.REACTANT
        );

        List<ReactionSubstance> products = createReactionSubstances(
                savedReaction,
                request.getProducts(),
                ReactionRole.PRODUCT
        );

        return ReactionMapper.toResponse(savedReaction, reactants, products);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ReactionResponse updateActiveStatus(String id, Boolean active) {
        ReactionDefinition reaction = reactionDefinitionRepository.findById(id)
                .orElseThrow(() -> new AppException(ReactionErrorCode.REACTION_NOT_FOUND, id));

        reaction.setActive(active);

        ReactionDefinition saved = reactionDefinitionRepository.save(reaction);

        return toResponseWithSubstances(saved);
    }

    private List<ReactionSubstance> createReactionSubstances(
            ReactionDefinition reaction,
            List<ReactionSubstanceRequest> requests,
            ReactionRole role
    ) {
        List<ReactionSubstance> result = new ArrayList<>();

        for (ReactionSubstanceRequest item : requests) {
            String formula = normalizeFormula(item.getFormula());

            ChemicalSubstance substance = chemicalSubstanceRepository.findByFormula(formula)
                    .orElseThrow(() -> new AppException(SubstanceErrorCode.SUBSTANCE_NOT_FOUND, formula));

            if (!Boolean.TRUE.equals(substance.getActive())) {
                throw new RuntimeException("Substance is inactive: " + formula);
            }

            ReactionSubstance reactionSubstance = ReactionSubstance.builder()
                    .reaction(reaction)
                    .substance(substance)
                    .role(role)
                    .coefficient(item.getCoefficient())
                    .build();

            result.add(reactionSubstance);
        }

        return reactionSubstanceRepository.saveAll(result);
    }

    private ReactionResponse toResponseWithSubstances(ReactionDefinition reaction) {
        List<ReactionSubstance> reactants =
                reactionSubstanceRepository.findByReactionIdAndRole(
                        reaction.getId(),
                        ReactionRole.REACTANT
                );

        List<ReactionSubstance> products =
                reactionSubstanceRepository.findByReactionIdAndRole(
                        reaction.getId(),
                        ReactionRole.PRODUCT
                );

        return ReactionMapper.toResponse(reaction, reactants, products);
    }

    private String normalizeCode(String code) {
        if (code == null) {
            throw new RuntimeException("Reaction code must not be null");
        }

        return code.trim().toUpperCase();
    }

    private String normalizeFormula(String formula) {
        if (formula == null) {
            throw new RuntimeException("Formula must not be null");
        }

        return formula.trim();
    }
}
