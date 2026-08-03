package com.chemistry.demo.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChemicalFormulaUtilsTest {

    @Test
    void buildsStableExactReactantKey() {
        assertEquals("Fe+HCl", ChemicalFormulaUtils.buildReactantKey(
                List.of(" HCl ", "Fe", "Fe")
        ));
    }

    @Test
    void preservesFormulaCase() {
        assertEquals("Fe+fe", ChemicalFormulaUtils.buildReactantKey(List.of("fe", "Fe")));
    }

    @Test
    void rejectsEmptyReactants() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ChemicalFormulaUtils.buildReactantKey(List.of())
        );
    }
}
