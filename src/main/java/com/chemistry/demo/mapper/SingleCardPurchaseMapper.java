package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.singleCardPurchase.MySingleCardPurchaseResponse;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.entity.SingleCard;
import com.chemistry.demo.entity.SingleCardPurchase;
import com.chemistry.demo.entity.UserAccess;
import com.chemistry.demo.enums.AccessStatus;

import java.time.Instant;

public class SingleCardPurchaseMapper {

    private SingleCardPurchaseMapper() {
    }

    public static MySingleCardPurchaseResponse toMyResponse(
            SingleCardPurchase purchase,
            UserAccess access,
            String qrImageUrl
    ) {
        if (purchase == null) {
            return null;
        }

        SingleCard singleCard = purchase.getSingleCard();
        ChemicalSubstance substance = singleCard != null
                ? singleCard.getSubstance()
                : null;

        Instant now = Instant.now();

        boolean active = access != null
                && access.getStatus() == AccessStatus.ACTIVE
                && access.getExpiredAt() != null
                && access.getExpiredAt().isAfter(now);

        return MySingleCardPurchaseResponse.builder()
                .purchaseId(purchase.getId())

                .singleCardId(singleCard != null ? singleCard.getId() : null)
                .singleCardCode(singleCard != null ? singleCard.getCode() : null)
                .singleCardName(singleCard != null ? singleCard.getName() : null)

                .substanceId(substance != null ? substance.getId() : null)
                .substanceFormula(substance != null ? substance.getFormula() : null)
                .substanceName(substance != null ? substance.getName() : null)
                .substanceVietnameseName(substance != null ? substance.getVietnameseName() : null)

                .qrContent(singleCard != null ? singleCard.getQrContent() : null)
                .qrImageUrl(qrImageUrl)

                .purchasedAt(purchase.getPurchasedAt())
                .startAt(access != null ? access.getStartAt() : null)
                .expiredAt(access != null ? access.getExpiredAt() : null)

                .purchaseStatus(
                        purchase.getStatus() != null
                                ? purchase.getStatus().name()
                                : null
                )
                .accessStatus(
                        access != null && access.getStatus() != null
                                ? access.getStatus().name()
                                : null
                )
                .active(active)
                .build();
    }
}