package com.chemistry.demo.dto.seed;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeedChemicalCardItem {

    private String formula;

    private String cardCode;

    private String qrPayload;

    private String displayName;

    private String imageUrl;

    private Boolean active;
}