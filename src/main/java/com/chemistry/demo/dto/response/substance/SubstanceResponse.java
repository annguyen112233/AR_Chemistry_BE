package com.chemistry.demo.dto.response.substance;

import com.chemistry.demo.dto.response.compoundDetail.CompoundDetailResponse;
import com.chemistry.demo.dto.response.elementDetail.ElementDetailResponse;
import com.chemistry.demo.enums.ChemicalGroup;
import com.chemistry.demo.enums.ChemicalSubstanceType;
import com.chemistry.demo.enums.SubstanceState;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubstanceResponse {

    private String id;

    private String formula;

    private String name;

    private String vietnameseName;

    private ChemicalSubstanceType type;

    private ChemicalGroup chemicalGroup;

    private SubstanceState state;

    private BigDecimal molarMass;

    private Boolean active;

    private Boolean includedInFullKit;

    private String description;

    private String safetyNote;

    private ElementDetailResponse elementDetail;

    private CompoundDetailResponse compoundDetail;
}
