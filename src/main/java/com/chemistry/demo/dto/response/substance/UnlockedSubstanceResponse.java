package com.chemistry.demo.dto.response.substance;

import com.chemistry.demo.enums.ChemicalGroup;
import com.chemistry.demo.enums.SubstanceState;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnlockedSubstanceResponse {

    private String substanceId;

    private String formula;

    private String name;

    private String vietnameseName;

    private ChemicalGroup chemicalGroup;

    private SubstanceState state;
}
