package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.chemical.ChemicalCardResponse;
import com.chemistry.demo.entity.ChemicalCard;
import com.chemistry.demo.entity.ChemicalSubstance;

public class ChemicalCardMapper {

    private ChemicalCardMapper() {
    }

    public static ChemicalCardResponse toResponse(ChemicalCard card) {
        if (card == null) {
            return null;
        }

        ChemicalSubstance substance = card.getSubstance();

        return ChemicalCardResponse.builder()
                .id(card.getId())
                .cardCode(card.getCardCode())
                .qrPayload(card.getQrPayload())
                .substanceId(substance != null ? substance.getId() : null)
                .formula(substance != null ? substance.getFormula() : null)
                .substanceName(substance != null ? substance.getName() : null)
                .vietnameseName(substance != null ? substance.getVietnameseName() : null)
                .type(substance != null ? substance.getType() : null)
                .chemicalGroup(substance != null ? substance.getChemicalGroup() : null)
                .state(substance != null ? substance.getState() : null)
                .displayName(card.getDisplayName())
                .imageUrl(card.getImageUrl())
                .frontImageUrl(card.getFrontImageKey())
                .backImageUrl(card.getBackImageKey())
                .active(card.getActive())
                .build();
    }
}
