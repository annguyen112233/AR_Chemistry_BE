package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.compoundDetail.CompoundDetailResponse;
import com.chemistry.demo.entity.CompoundDetail;

public class CompoundDetailMapper {

    private CompoundDetailMapper() {
    }

    public static CompoundDetailResponse toResponse(CompoundDetail detail) {
        if (detail == null) {
            return null;
        }

        return CompoundDetailResponse.builder()
                .iupacName(detail.getIupacName())
                .casNumber(detail.getCasNumber())
                .compoundClass(detail.getCompoundClass())
                .usageNote(detail.getUsageNote())
                .reactionProductOnly(detail.getReactionProductOnly())
                .physicalInKit(detail.getPhysicalInKit())
                .build();
    }
}
