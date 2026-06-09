package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.inventory.UserInventoryResponse;
import com.chemistry.demo.dto.response.substance.UnlockedSubstanceResponse;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.entity.UserInventory;

public class InventoryMapper {

    private InventoryMapper() {
    }

    public static UserInventoryResponse toResponse(UserInventory inventory) {
        if (inventory == null) {
            return null;
        }

        ChemicalSubstance substance = inventory.getSubstance();

        return UserInventoryResponse.builder()
                .id(inventory.getId())
                .userId(
                        inventory.getUser() != null
                                ? inventory.getUser().getCognitoSub()
                                : null
                )
                .substanceId(substance != null ? substance.getId() : null)
                .formula(substance != null ? substance.getFormula() : null)
                .name(substance != null ? substance.getName() : null)
                .vietnameseName(substance != null ? substance.getVietnameseName() : null)
                .chemicalGroup(substance != null ? substance.getChemicalGroup() : null)
                .state(substance != null ? substance.getState() : null)
                .source(inventory.getSource())
                .sourceRef(inventory.getSourceRef())
                .quantity(inventory.getQuantity())
                .active(inventory.getActive())
                .acquiredAt(inventory.getAcquiredAt())
                .expiresAt(inventory.getExpiresAt())
                .build();
    }

    public static UnlockedSubstanceResponse toUnlockedSubstance(ChemicalSubstance substance) {
        if (substance == null) {
            return null;
        }

        return UnlockedSubstanceResponse.builder()
                .substanceId(substance.getId())
                .formula(substance.getFormula())
                .name(substance.getName())
                .vietnameseName(substance.getVietnameseName())
                .chemicalGroup(substance.getChemicalGroup())
                .state(substance.getState())
                .build();
    }
}
