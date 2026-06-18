package com.chemistry.demo.dto.seed;

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
public class SeedCompoundItem {

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

    private String iupacName;

    private String casNumber;

    private String compoundClass;

    private String usageNote;

    private Boolean reactionProductOnly;

    private Boolean physicalInKit;
}
