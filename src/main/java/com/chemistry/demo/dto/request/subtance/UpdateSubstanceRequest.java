package com.chemistry.demo.dto.request.subtance;

import com.chemistry.demo.enums.ChemicalGroup;
import com.chemistry.demo.enums.ChemicalSubstanceType;
import com.chemistry.demo.enums.SubstanceState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSubstanceRequest {

    @NotBlank
    private String name;

    private String vietnameseName;

    @NotNull
    private ChemicalSubstanceType type;

    @NotNull
    private ChemicalGroup chemicalGroup;

    @NotNull
    private SubstanceState state;

    private BigDecimal molarMass;

    private Boolean active;

    private Boolean includedInFullKit;

    private String description;

    private String safetyNote;
}
