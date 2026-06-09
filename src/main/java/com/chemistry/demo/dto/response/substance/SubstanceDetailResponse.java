package com.chemistry.demo.dto.response.substance;

import com.chemistry.demo.dto.response.compoundDetail.CompoundDetailResponse;
import com.chemistry.demo.dto.response.elementDetail.ElementDetailResponse;
import com.chemistry.demo.enums.ChemicalGroup;
import com.chemistry.demo.enums.SubstanceState;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubstanceDetailResponse {

    private String substanceId;

    private String formula;

    private String name;

    private String vietnameseName;

    private ChemicalGroup chemicalGroup;

    private SubstanceState state;

    private ElementDetailResponse elementDetail;

    private CompoundDetailResponse compoundDetail;
}