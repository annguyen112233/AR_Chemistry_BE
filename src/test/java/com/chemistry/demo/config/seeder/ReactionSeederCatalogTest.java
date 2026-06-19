package com.chemistry.demo.config.seeder;

import com.chemistry.demo.dto.seed.SeedReactionItem;
import com.chemistry.demo.dto.seed.SeedReactionSubstanceItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReactionSeederCatalogTest {

    @Test
    void rejectsDifferentReactionCodesWithSameExactReactants() {
        List<SeedReactionItem> items = List.of(
                reaction("FE_S_DISH", "Fe", "S"),
                reaction("FE_S_TUBE", "S", "Fe")
        );

        assertThrows(
                IllegalStateException.class,
                () -> ReactionSeeder.validateUniqueReactantKeys(items)
        );
    }

    @Test
    void acceptsDistinctExactReactantSets() {
        assertDoesNotThrow(() -> ReactionSeeder.validateUniqueReactantKeys(List.of(
                reaction("C_O2", "C"),
                reaction("FE_S", "Fe", "S")
        )));
    }

    private SeedReactionItem reaction(String code, String... formulas) {
        return SeedReactionItem.builder()
                .code(code)
                .reactants(java.util.Arrays.stream(formulas)
                        .map(formula -> SeedReactionSubstanceItem.builder()
                                .formula(formula)
                                .coefficient(1)
                                .build())
                        .toList())
                .build();
    }
}
