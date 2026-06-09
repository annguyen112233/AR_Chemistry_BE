package com.chemistry.demo.services.singleCardPurchase.Impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.singleCardPurchase.MySingleCardPurchaseResponse;
import com.chemistry.demo.dto.response.singleCardPurchase.SingleCardPurchaseResponse;
import com.chemistry.demo.entity.SingleCard;
import com.chemistry.demo.entity.SingleCardPurchase;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.entity.UserAccess;
import com.chemistry.demo.enums.*;
import com.chemistry.demo.mapper.SingleCardPurchaseMapper;
import com.chemistry.demo.repository.SingleCardPurchaseRepository;
import com.chemistry.demo.repository.SingleCardRepository;
import com.chemistry.demo.repository.UserAccessRepository;
import com.chemistry.demo.services.aws.S3Service;
import com.chemistry.demo.utils.PageResponseUtils;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class SingleCardPurchaseServiceImpl implements com.chemistry.demo.services.singleCardPurchase.SingleCardPurchaseService {

    private final SingleCardRepository singleCardRepository;
    private final SingleCardPurchaseRepository singleCardPurchaseRepository;
    private final UserAccessRepository userAccessRepository;

    private final S3Service s3Service;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public SingleCardPurchaseResponse fakeBuySingleCard(String singleCardId) {
        User user = securityUtils.getCurrentUserCognitoSub();

        SingleCard singleCard = singleCardRepository.findById(singleCardId)
                .orElseThrow(() -> new RuntimeException("Single card not found"));

        if (!Boolean.TRUE.equals(singleCard.getActive())) {
            throw new RuntimeException("Single card is not active");
        }

        Instant now = Instant.now();
        Instant expiredAt = now.plus(singleCard.getDurationDays(), ChronoUnit.DAYS);

        SingleCardPurchase purchase = SingleCardPurchase.builder()
                .user(user)
                .singleCard(singleCard)
                .paymentProvider(PaymentProvider.DEV_FAKE_PAYMENT)
                .status(PurchaseStatus.PAID)
                .price(singleCard.getPrice())
                .googlePlayProductId(singleCard.getGooglePlayProductId())
                .purchasedAt(now)
                .build();

        purchase = singleCardPurchaseRepository.save(purchase);

        UserAccess userAccess = UserAccess.builder()
                .user(user)
                .accessType(AccessType.SINGLE_SUBSTANCE_AR_30_DAYS)
                .source(AccessSource.SINGLE_CARD_PURCHASE)
                .referenceId(purchase.getId())
                .startAt(now)
                .expiredAt(expiredAt)
                .status(AccessStatus.ACTIVE)
                .build();

        userAccessRepository.save(userAccess);

        String qrImageUrl = null;

        if (singleCard.getQrS3Key() != null && !singleCard.getQrS3Key().isBlank()) {
            qrImageUrl = s3Service.generatePresignedGetUrl(singleCard.getQrS3Key());
        }

        return SingleCardPurchaseResponse.builder()
                .purchaseId(purchase.getId())
                .singleCardId(singleCard.getId())
                .singleCardName(singleCard.getName())
                .substanceId(singleCard.getSubstance().getId())
                .substanceName(singleCard.getSubstance().getName())
                .substanceFormula(singleCard.getSubstance().getFormula())
                .qrContent(singleCard.getQrContent())
                .qrImageUrl(qrImageUrl)
                .purchasedAt(purchase.getPurchasedAt())
                .expiredAt(expiredAt)
                .status(purchase.getStatus().name())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MySingleCardPurchaseResponse> getMySingleCardPurchases(Pageable pageable) {
        User user = securityUtils.getCurrentUserCognitoSub();

        Page<SingleCardPurchase> purchases =
                singleCardPurchaseRepository.findByUserAndStatusOrderByPurchasedAtDesc(
                        user,
                        PurchaseStatus.PAID,
                        pageable
                );

        return PageResponseUtils.toPageResponse(
                purchases,
                purchase -> {
                    UserAccess access = userAccessRepository
                            .findByUserAndAccessTypeAndSourceAndReferenceId(
                                    user,
                                    AccessType.SINGLE_SUBSTANCE_AR_30_DAYS,
                                    AccessSource.SINGLE_CARD_PURCHASE,
                                    purchase.getId()
                            )
                            .orElse(null);

                    String qrImageUrl = null;

                    SingleCard singleCard = purchase.getSingleCard();
                    if (singleCard != null
                            && singleCard.getQrS3Key() != null
                            && !singleCard.getQrS3Key().isBlank()) {
                        qrImageUrl = s3Service.generatePresignedGetUrl(singleCard.getQrS3Key());
                    }

                    return SingleCardPurchaseMapper.toMyResponse(
                            purchase,
                            access,
                            qrImageUrl
                    );
                }
        );
    }
}
