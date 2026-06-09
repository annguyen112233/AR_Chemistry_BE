package com.chemistry.demo.dto.response.singleCardPurchase;

import java.time.Instant;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SingleCardPurchaseResponse {
    private String purchaseId;

    private String singleCardId;
    private String singleCardName;

    private String substanceId;
    private String substanceName;
    private String substanceFormula;

    private String qrContent;


    private String qrImageUrl;

    private Instant purchasedAt;
    private Instant expiredAt;

    private String status;
}
