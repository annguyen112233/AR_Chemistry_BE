package com.chemistry.demo.services.reaction.Impl;

import com.chemistry.demo.dto.request.reaction.CheckReactionRequest;
import com.chemistry.demo.dto.response.knowledgePoint.ArScanRewardResponse;
import com.chemistry.demo.dto.response.reaction.CheckReactionResponse;
import com.chemistry.demo.entity.*;
import com.chemistry.demo.enums.ReactionRole;
import com.chemistry.demo.mapper.ReactionMapper;
import com.chemistry.demo.repository.ChemicalCardRepository;
import com.chemistry.demo.repository.ReactionSubstanceRepository;
import com.chemistry.demo.services.knowledgePoint.KnowledgePointService;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReactionCheckServiceImpl implements com.chemistry.demo.services.reaction.ReactionCheckService {
    private final SecurityUtils securityUtils;
    private final ArAccessServiceImpl arAccessService;
    private final ReactionSubstanceRepository reactionSubstanceRepository;
    private final ChemicalCardRepository chemicalCardRepository;
    private final KnowledgePointService knowledgePointService;
    @Override
    @Transactional
    public CheckReactionResponse checkReaction(CheckReactionRequest request) {
        User user = securityUtils.getCurrentUserCognitoSub();


        List<String> qrPayloads = request.getQrPayloads()
                .stream()
                .map(this::normalizeQrPayload)
                .filter(payload -> !payload.isBlank())
                .distinct()
                .toList();

        if (qrPayloads.isEmpty()) {
            return CheckReactionResponse.builder()
                    .matched(false)
                    .reason("EMPTY_INPUT")
                    .message("Chưa có QR nào được quét.")
                    .missingSubstances(List.of())
                    .reactants(List.of())
                    .products(List.of())
                    .build();
        }

        List<ChemicalCard> cards = qrPayloads.stream()
                .map(payload -> chemicalCardRepository.findByQrPayload(payload).orElse(null))
                .toList();

        List<String> notFoundQrPayloads = qrPayloads.stream()
                .filter(payload -> cards.stream()
                        .noneMatch(card -> card != null && card.getQrPayload().equals(payload)))
                .toList();

        if (!notFoundQrPayloads.isEmpty()) {
            return CheckReactionResponse.builder()
                    .matched(false)
                    .reason("QR_CARD_NOT_FOUND")
                    .message("Có QR không tồn tại trong hệ thống.")
                    .missingSubstances(notFoundQrPayloads)
                    .reactants(List.of())
                    .products(List.of())
                    .build();
        }

        List<String> inactiveCards = cards.stream()
                .filter(card -> !Boolean.TRUE.equals(card.getActive()))
                .map(ChemicalCard::getQrPayload)
                .toList();

        if (!inactiveCards.isEmpty()) {
            return CheckReactionResponse.builder()
                    .matched(false)
                    .reason("QR_CARD_INACTIVE")
                    .message("Có QR/card đang bị tắt trong hệ thống.")
                    .missingSubstances(inactiveCards)
                    .reactants(List.of())
                    .products(List.of())
                    .build();
        }

        List<ChemicalSubstance> substances = cards.stream()
                .map(ChemicalCard::getSubstance)
                .distinct()
                .toList();

        List<String> inactiveSubstances = substances.stream()
                .filter(substance -> !Boolean.TRUE.equals(substance.getActive()))
                .map(ChemicalSubstance::getFormula)
                .toList();

        if (!inactiveSubstances.isEmpty()) {
            return CheckReactionResponse.builder()
                    .matched(false)
                    .reason("SUBSTANCE_INACTIVE")
                    .message("Có chất đang bị tắt trong hệ thống.")
                    .missingSubstances(inactiveSubstances)
                    .reactants(List.of())
                    .products(List.of())
                    .build();
        }

        if (!arAccessService.canScanSubstances(user, substances)) {
            return CheckReactionResponse.builder()
                    .matched(false)
                    .reason("AR_ACCESS_REQUIRED")
                    .message("Bạn chưa có quyền quét một hoặc nhiều chất trong phản ứng này.")
                    .missingSubstances(
                            substances.stream()
                                    .map(ChemicalSubstance::getFormula)
                                    .toList()
                    )
                    .reactants(List.of())
                    .products(List.of())
                    .build();
        }

        List<String> inputFormulas = substances.stream()
                .map(ChemicalSubstance::getFormula)
                .distinct()
                .toList();

        List<ReactionDefinition> candidates =
                reactionSubstanceRepository.findCandidateReactionsByReactantFormulas(inputFormulas);

        Set<String> inputSet = new HashSet<>(inputFormulas);

        for (ReactionDefinition candidate : candidates) {
            List<ReactionSubstance> reactants =
                    reactionSubstanceRepository.findByReactionIdAndRole(
                            candidate.getId(),
                            ReactionRole.REACTANT
                    );

            Set<String> requiredReactantSet = reactants.stream()
                    .map(rs -> rs.getSubstance().getFormula())
                    .collect(java.util.stream.Collectors.toSet());

            if (requiredReactantSet.equals(inputSet)) {
                List<ReactionSubstance> products =
                        reactionSubstanceRepository.findByReactionIdAndRole(
                                candidate.getId(),
                                ReactionRole.PRODUCT
                        );

                ArScanRewardResponse reward =
                        knowledgePointService.rewardArScan("REACTION:" + candidate.getCode());

                return CheckReactionResponse.builder()
                        .matched(true)
                        .reason("REACTION_MATCHED")
                        .message("Tìm thấy phản ứng phù hợp.")
                        .reactionId(candidate.getId())
                        .reactionCode(candidate.getCode())
                        .equation(candidate.getEquation())
                        .reactionType(candidate.getReactionType())
                        .arSceneKey(candidate.getArSceneKey())
                        .reward(reward)
                        .missingSubstances(List.of())
                        .reactants(
                                reactants.stream()
                                        .map(ReactionMapper::toSubstanceResponse)
                                        .toList()
                        )
                        .products(
                                products.stream()
                                        .map(ReactionMapper::toSubstanceResponse)
                                        .toList()
                        )
                        .build();
            }
        }

        return CheckReactionResponse.builder()
                .matched(false)
                .reason("NO_REACTION")
                .message("Không tìm thấy phản ứng phù hợp với các QR đã quét.")
                .missingSubstances(List.of())
                .reactants(List.of())
                .products(List.of())
                .build();
    }

    private String normalizeQrPayload(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}
