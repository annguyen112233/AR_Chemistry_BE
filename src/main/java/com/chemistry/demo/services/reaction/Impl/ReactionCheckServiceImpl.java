package com.chemistry.demo.services.reaction.Impl;

import com.chemistry.demo.dto.request.reaction.CheckReactionRequest;
import com.chemistry.demo.dto.response.reaction.CheckReactionResponse;
import com.chemistry.demo.entity.ChemicalCard;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.entity.ReactionDefinition;
import com.chemistry.demo.entity.ReactionSubstance;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.ReactionRole;
import com.chemistry.demo.mapper.ReactionMapper;
import com.chemistry.demo.repository.ChemicalCardRepository;
import com.chemistry.demo.repository.ReactionDefinitionRepository;
import com.chemistry.demo.repository.ReactionSubstanceRepository;
import com.chemistry.demo.services.reaction.ReactionCheckService;
import com.chemistry.demo.utils.ChemicalFormulaUtils;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactionCheckServiceImpl implements ReactionCheckService {
    private final SecurityUtils securityUtils;
    private final ArAccessServiceImpl arAccessService;
    private final ReactionSubstanceRepository reactionSubstanceRepository;
    private final ReactionDefinitionRepository reactionDefinitionRepository;
    private final ChemicalCardRepository chemicalCardRepository;

    @Override
    @Transactional(readOnly = true)
    public CheckReactionResponse checkReaction(CheckReactionRequest request) {
        User user = securityUtils.getCurrentUserCognitoSub();

        List<String> qrPayloads = request.getQrPayloads().stream()
                .map(this::normalizeQrPayload)
                .filter(payload -> !payload.isBlank())
                .distinct()
                .sorted()
                .toList();

        if (qrPayloads.isEmpty()) {
            return rejected("EMPTY_INPUT", "Chưa có QR nào được quét.", List.of(), List.of());
        }

        List<ChemicalCard> cards = qrPayloads.stream()
                .map(payload -> chemicalCardRepository.findByQrPayload(payload).orElse(null))
                .toList();

        List<String> notFoundQrPayloads = qrPayloads.stream()
                .filter(payload -> cards.stream()
                        .noneMatch(card -> card != null && card.getQrPayload().equals(payload)))
                .toList();
        if (!notFoundQrPayloads.isEmpty()) {
            return rejected(
                    "QR_CARD_NOT_FOUND",
                    "Không tìm thấy thẻ QR: " + String.join(", ", notFoundQrPayloads),
                    notFoundQrPayloads,
                    List.of()
            );
        }

        List<String> inactiveCards = cards.stream()
                .filter(card -> !Boolean.TRUE.equals(card.getActive()))
                .map(ChemicalCard::getQrPayload)
                .sorted()
                .toList();
        if (!inactiveCards.isEmpty()) {
            return rejected(
                    "QR_CARD_INACTIVE",
                    "Thẻ QR đang tạm ngưng: " + String.join(", ", inactiveCards),
                    inactiveCards,
                    List.of()
            );
        }

        List<ChemicalSubstance> substances = cards.stream()
                .map(ChemicalCard::getSubstance)
                .distinct()
                .toList();

        List<String> inactiveFormulas = substances.stream()
                .filter(substance -> !Boolean.TRUE.equals(substance.getActive()))
                .map(ChemicalSubstance::getFormula)
                .sorted()
                .toList();
        if (!inactiveFormulas.isEmpty()) {
            return rejected(
                    "SUBSTANCE_INACTIVE",
                    "Chất đang tạm ngưng: " + String.join(", ", inactiveFormulas),
                    List.of(),
                    inactiveFormulas
            );
        }

        List<String> formulas = ChemicalFormulaUtils.normalizeDistinctFormulas(substances.stream()
                .map(ChemicalSubstance::getFormula)
                .toList());

        if (!arAccessService.canScanSubstances(user, substances)) {
            return rejected(
                    "AR_ACCESS_REQUIRED",
                    "Bạn chưa có quyền quét một hoặc nhiều chất trong phản ứng này.",
                    List.of(),
                    formulas
            );
        }

        String reactantKey = ChemicalFormulaUtils.buildReactantKey(formulas);
        ReactionDefinition candidate = reactionDefinitionRepository
                .findByReactantKey(reactantKey)
                .filter(reaction -> Boolean.TRUE.equals(reaction.getActive()))
                .orElse(null);

        if (candidate == null) {
            return rejected(
                    "NO_REACTION",
                    "Không tìm thấy phản ứng phù hợp với các QR đã quét.",
                    List.of(),
                    List.of()
            );
        }

        List<ReactionSubstance> reactants = reactionSubstanceRepository.findByReactionIdAndRole(
                candidate.getId(),
                ReactionRole.REACTANT
        );
        String storedReactantKey = ChemicalFormulaUtils.buildReactantKey(reactants.stream()
                .map(item -> item.getSubstance().getFormula())
                .toList());
        if (!storedReactantKey.equals(reactantKey)) {
            throw new IllegalStateException("Reaction catalog key mismatch for " + candidate.getCode());
        }

        List<ReactionSubstance> products = reactionSubstanceRepository.findByReactionIdAndRole(
                candidate.getId(),
                ReactionRole.PRODUCT
        );

        return CheckReactionResponse.builder()
                .matched(true)
                .reason("REACTION_MATCHED")
                .message("Tìm thấy phản ứng phù hợp.")
                .reactionId(candidate.getId())
                .reactionCode(candidate.getCode())
                .equation(candidate.getEquation())
                .reactionType(candidate.getReactionType())
                .arSceneKey(candidate.getArSceneKey())
                .missingSubstances(List.of())
                .affectedQrPayloads(List.of())
                .affectedFormulas(List.of())
                .reactants(reactants.stream().map(ReactionMapper::toSubstanceResponse).toList())
                .products(products.stream().map(ReactionMapper::toSubstanceResponse).toList())
                .build();
    }

    private String normalizeQrPayload(String value) {
        return value == null ? "" : value.trim();
    }

    private CheckReactionResponse rejected(
            String reason,
            String message,
            List<String> affectedQrPayloads,
            List<String> affectedFormulas
    ) {
        List<String> legacyAffected = affectedQrPayloads.isEmpty()
                ? affectedFormulas
                : affectedQrPayloads;

        return CheckReactionResponse.builder()
                .matched(false)
                .reason(reason)
                .message(message)
                .missingSubstances(legacyAffected)
                .affectedQrPayloads(affectedQrPayloads)
                .affectedFormulas(affectedFormulas)
                .reactants(List.of())
                .products(List.of())
                .build();
    }
}
