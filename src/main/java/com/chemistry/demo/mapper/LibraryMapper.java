package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.library.LibraryCardResponse;
import com.chemistry.demo.entity.ChemicalSubstance;

public class LibraryMapper {

    private LibraryMapper() {
    }

    public static LibraryCardResponse toLibraryCardResponse(ChemicalSubstance substance) {
        if (substance == null) {
            return null;
        }

        return LibraryCardResponse.builder()
                .substanceId(substance.getId())
                .formula(substance.getFormula())
                .name(substance.getName())
                .vietnameseName(substance.getVietnameseName())
                .type(substance.getType())
                .chemicalGroup(substance.getChemicalGroup())
                .state(substance.getState())
                .molarMass(substance.getMolarMass())
                .includedInFullKit(substance.getIncludedInFullKit())
                .build();
    }
}