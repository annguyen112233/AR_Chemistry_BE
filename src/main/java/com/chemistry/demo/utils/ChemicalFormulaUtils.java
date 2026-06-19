package com.chemistry.demo.utils;

import java.util.Collection;
import java.util.List;

public final class ChemicalFormulaUtils {

    private ChemicalFormulaUtils() {
    }

    public static String normalizeFormula(String formula) {
        if (formula == null || formula.isBlank()) {
            throw new IllegalArgumentException("Chemical formula must not be blank");
        }
        return formula.trim();
    }

    public static List<String> normalizeDistinctFormulas(Collection<String> formulas) {
        if (formulas == null) {
            throw new IllegalArgumentException("Chemical formulas must not be null");
        }
        return formulas.stream()
                .map(ChemicalFormulaUtils::normalizeFormula)
                .distinct()
                .sorted()
                .toList();
    }

    public static String buildReactantKey(Collection<String> formulas) {
        List<String> normalized = normalizeDistinctFormulas(formulas);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("At least one reactant formula is required");
        }
        return String.join("+", normalized);
    }
}
