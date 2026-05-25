package com.chemistry.demo.dto.response.chemical;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChemicalCardResponse {

    private String id;

    private Integer atomicNumber;

    private String symbol;

    private String name;

    private String category;

    private BigDecimal atomicMass;

    private Integer period;

    private Integer groupNumber;

    private BigDecimal price;

    private Boolean active;

    private Boolean purchasable;
}