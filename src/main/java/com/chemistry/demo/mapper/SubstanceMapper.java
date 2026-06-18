package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.substance.SubstanceResponse;
import com.chemistry.demo.entity.ChemicalSubstance;

public class SubstanceMapper {

    private SubstanceMapper() {
    }

    public static SubstanceResponse toResponse(ChemicalSubstance substance) {
        if (substance == null) {
            return null;
        }

        return SubstanceResponse.builder()
                .id(substance.getId())
                .formula(substance.getFormula())
                .name(substance.getName())
                .vietnameseName(substance.getVietnameseName())
                .type(substance.getType())
                .chemicalGroup(substance.getChemicalGroup())
                .state(substance.getState())
                .molarMass(substance.getMolarMass())
                .active(substance.getActive())
                .includedInFullKit(substance.getIncludedInFullKit())
                .description(substance.getDescription())
                .safetyNote(substance.getSafetyNote())
                .build();
    }
}
