package com.chemistry.demo.dto.response.elementDetail;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElementDetailResponse {

    private Integer atomicNumber;

    private String symbol;

    private String periodicCategory;

    private BigDecimal atomicMass;

    private Integer period;

    private Integer groupNumber;
}