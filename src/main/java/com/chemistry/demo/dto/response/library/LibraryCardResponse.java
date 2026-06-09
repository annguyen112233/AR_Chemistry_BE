package com.chemistry.demo.dto.response.library;

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
public class LibraryCardResponse {

    private String substanceId;

    private String formula;

    private String name;

    private String vietnameseName;

    private ChemicalSubstanceType type;

    private ChemicalGroup chemicalGroup;

    private SubstanceState state;

    private BigDecimal molarMass;

    private Boolean includedInFullKit;
}