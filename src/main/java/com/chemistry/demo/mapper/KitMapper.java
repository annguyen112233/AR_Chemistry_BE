package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.kit.KitResponse;
import com.chemistry.demo.dto.response.substance.SubstanceResponse;
import com.chemistry.demo.entity.Kit;
import com.chemistry.demo.entity.KitItem;

import java.util.List;

public class KitMapper {

    private KitMapper() {
    }

    public static KitResponse toResponse(
            Kit kit,
            List<KitItem> kitItems
    ) {
        if (kit == null) {
            return null;
        }

        List<SubstanceResponse> items = kitItems == null
                ? List.of()
                : kitItems.stream()
                .map(KitItem::getSubstance)
                .map(SubstanceMapper::toResponse)
                .toList();

        return KitResponse.builder()
                .id(kit.getId())
                .code(kit.getCode())
                .name(kit.getName())
                .description(kit.getDescription())
                .price(kit.getPrice())
                .active(kit.getActive())
                .items(items)
                .build();
    }
}
