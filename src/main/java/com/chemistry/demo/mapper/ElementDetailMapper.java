package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.elementDetail.ElementDetailResponse;
import com.chemistry.demo.entity.ElementDetail;

public class ElementDetailMapper {

    private ElementDetailMapper() {
    }

    public static ElementDetailResponse toResponse(ElementDetail detail) {
        if (detail == null) {
            return null;
        }

        return ElementDetailResponse.builder()
                .atomicNumber(detail.getAtomicNumber())
                .symbol(detail.getSymbol())
                .periodicCategory(detail.getPeriodicCategory())
                .atomicMass(detail.getAtomicMass())
                .period(detail.getPeriod())
                .groupNumber(detail.getGroupNumber())
                .build();
    }
}
