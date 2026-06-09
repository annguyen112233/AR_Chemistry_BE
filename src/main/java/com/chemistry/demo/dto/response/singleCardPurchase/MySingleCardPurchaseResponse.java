package com.chemistry.demo.dto.response.singleCardPurchase;

import lombok.*;

import java.time.Instant;
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MySingleCardPurchaseResponse {
    private String purchaseId;

    private String singleCardId;
    private String singleCardCode;
    private String singleCardName;

    private String substanceId;
    private String substanceFormula;
    private String substanceName;
    private String substanceVietnameseName;

    private String qrContent;
    private String qrImageUrl;

    private Instant purchasedAt;
    private Instant startAt;
    private Instant expiredAt;

    private String purchaseStatus;
    private String accessStatus;
    private Boolean active;
}
