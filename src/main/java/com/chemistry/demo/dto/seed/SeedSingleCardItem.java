package com.chemistry.demo.dto.seed;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SeedSingleCardItem {

    private String formula;

    private String cardCode;

    private String qrPayload;

    private String qrS3Key;

    private String name;

    private String description;

    private Long kpPrice;

    private Integer durationDays;

    private Boolean active;

    private String googlePlayProductId;
}