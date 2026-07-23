package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.reaction.ReactionResponse;
import com.chemistry.demo.dto.response.reaction.ReactionSubstanceResponse;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.entity.ReactionDefinition;
import com.chemistry.demo.entity.ReactionSubstance;

import java.util.List;
import java.util.Objects;

public class ReactionMapper {

    private ReactionMapper() {
    }

    public static ReactionSubstanceResponse toSubstanceResponse(
            ReactionSubstance reactionSubstance
    ) {
        if (reactionSubstance == null
                || reactionSubstance.getSubstance() == null) {
            return null;
        }

        ChemicalSubstance substance =
                reactionSubstance.getSubstance();

        return ReactionSubstanceResponse.builder()
                .substanceId(substance.getId())
                .formula(substance.getFormula())
                .name(substance.getName())
                .vietnameseName(
                        substance.getVietnameseName()
                )
                .chemicalGroup(
                        substance.getChemicalGroup()
                )
                .state(substance.getState())
                .coefficient(
                        reactionSubstance.getCoefficient()
                )
                .substanceOrder(
                        reactionSubstance.getSubstanceOrder()
                )
                .build();
    }

    public static ReactionResponse toResponse(
            ReactionDefinition reaction,
            List<ReactionSubstance> reactants,
            List<ReactionSubstance> products
    ) {
        if (reaction == null) {
            return null;
        }

        return ReactionResponse.builder()
                .id(reaction.getId())
                .code(reaction.getCode())
                .name(reaction.getName())
                .equation(reaction.getEquation())
                .reactionCategory(
                        reaction.getReactionCategory()
                )
                .reactionType(
                        reaction.getReactionType()
                )
                .arSceneKey(
                        reaction.getArSceneKey()
                )
                .description(
                        reaction.getDescription()
                )
                .script(
                        reaction.getScript()
                )
                .grade(
                        reaction.getGrade()
                )
                .active(
                        reaction.getActive()
                )
                .reactants(
                        mapSubstances(reactants)
                )
                .products(
                        mapSubstances(products)
                )
                .build();
    }

    private static List<ReactionSubstanceResponse>
    mapSubstances(
            List<ReactionSubstance> substances
    ) {
        if (substances == null
                || substances.isEmpty()) {
            return List.of();
        }

        return substances.stream()
                .map(
                        ReactionMapper
                                ::toSubstanceResponse
                )
                .filter(Objects::nonNull)
                .toList();
    }
}