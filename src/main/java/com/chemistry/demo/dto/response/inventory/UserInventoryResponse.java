package com.chemistry.demo.dto.response.inventory;

import com.chemistry.demo.enums.ChemicalGroup;
import com.chemistry.demo.enums.InventorySource;
import com.chemistry.demo.enums.SubstanceState;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInventoryResponse {

    private String id;

    private String userId;

    private String substanceId;

    private String formula;

    private String name;

    private String vietnameseName;

    private ChemicalGroup chemicalGroup;

    private SubstanceState state;

    private InventorySource source;

    private String sourceRef;

    private Integer quantity;

    private Boolean active;

    private Instant acquiredAt;

    private Instant expiresAt;
}
