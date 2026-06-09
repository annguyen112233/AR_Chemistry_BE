package com.chemistry.demo.dto.response.singleCard;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleCardShopResponse {
    private String id;

    private String code;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer durationDays;

    private Boolean active;

    private String googlePlayProductId;

    private String substanceId;

    private String substanceFormula;

    private String substanceName;

    private String substanceVietnameseName;
}
