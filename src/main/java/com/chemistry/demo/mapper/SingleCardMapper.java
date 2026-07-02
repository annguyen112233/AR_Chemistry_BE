package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.singleCard.SingleCardShopResponse;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.entity.SingleCard;

public class SingleCardMapper {

    private SingleCardMapper() {
    }

    public static SingleCardShopResponse toShopResponse(SingleCard singleCard) {
        if (singleCard == null) {
            return null;
        }

        ChemicalSubstance substance = singleCard.getSubstance();

        return SingleCardShopResponse.builder()
                .id(singleCard.getId())
                .code(singleCard.getCode())
                .name(singleCard.getName())
                .description(singleCard.getDescription())
                .kpPrice(singleCard.getKpPrice())
                .durationDays(singleCard.getDurationDays())
                .active(singleCard.getActive())
                .substanceId(substance != null ? substance.getId() : null)
                .substanceFormula(substance != null ? substance.getFormula() : null)
                .substanceName(substance != null ? substance.getName() : null)
                .substanceVietnameseName(substance != null ? substance.getVietnameseName() : null)
                .frontImageUrl(null)
                .backImageUrl(null)
                .build();
    }
}