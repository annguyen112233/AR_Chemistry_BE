package com.chemistry.demo.dto.seed;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeedKitItem {

    private String code;

    private String name;

    private String description;

    private BigDecimal price;

    private Boolean active;
}