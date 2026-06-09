package com.chemistry.demo.dto.response.chemical;

import com.chemistry.demo.enums.ChemicalGroup;
import com.chemistry.demo.enums.ChemicalSubstanceType;
import com.chemistry.demo.enums.SubstanceState;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChemicalCardResponse {

    private String id;

    private String cardCode;

    private String qrPayload;

    private String substanceId;

    private String formula;

    private String substanceName;

    private String vietnameseName;

    private ChemicalSubstanceType type;

    private ChemicalGroup chemicalGroup;

    private SubstanceState state;

    private String displayName;

    private String imageUrl;

    private Boolean active;
}