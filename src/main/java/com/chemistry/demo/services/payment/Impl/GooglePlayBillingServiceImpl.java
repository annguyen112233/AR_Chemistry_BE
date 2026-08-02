package com.chemistry.demo.services.payment.Impl;

import com.chemistry.demo.dto.request.payment.GooglePlayVerifyRequest;
import com.chemistry.demo.dto.response.reaction.ArAccessResponse;
import com.chemistry.demo.entity.Packages;
import com.chemistry.demo.entity.Payment;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.entity.UserAccess;
import com.chemistry.demo.enums.*;
import com.chemistry.demo.repository.PackageRepository;
import com.chemistry.demo.services.payment.GooglePlayBillingService;
import com.chemistry.demo.services.payment.GooglePlayVerifier;
import com.chemistry.demo.utils.SecurityUtils;
import com.google.api.services.androidpublisher.model.ProductPurchase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GooglePlayBillingServiceImpl implements GooglePlayBillingService {
    private final SecurityUtils securityUtils;
    private final PackageRepository packagesRepository;
    private final com.chemistry.demo.repository.PaymentRepository paymentRepository;
    private final com.chemistry.demo.repository.UserAccessRepository userAccessRepository;
    private final com.chemistry.demo.services.reaction.ArAccessService arAccessService;
    private final GooglePlayVerifier googlePlayVerifier;
    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT')")
    @com.chemistry.demo.aspect.AuditEvent("PAYMENT_VERIFIED")
    public ArAccessResponse verify(GooglePlayVerifyRequest request) {
        User user = securityUtils.getCurrentUserCognitoSub();

        Packages packageEntity = packagesRepository.findByGoogleProductId(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Package not found"));

        Optional<Payment> existingPaymentOpt =
                paymentRepository.findByGooglePurchaseToken(request.getPurchaseToken());

        if (existingPaymentOpt.isPresent()) {
            Payment existingPayment = existingPaymentOpt.get();

            boolean alreadyGranted = userAccessRepository.existsByReferenceIdAndSource(
                    existingPayment.getId(),
                    AccessSource.GOOGLE_PLAY_PURCHASE
            );

            if (!alreadyGranted) {
                grantGooglePlayAr30Days(user, existingPayment, packageEntity);
            }

            return arAccessService.getMyArAccess();
        }

        ProductPurchase productPurchase = googlePlayVerifier.verifyProductPurchase(
                request.getProductId(),
                request.getPurchaseToken()
        );

        Integer purchaseState = productPurchase.getPurchaseState();

        log.info(
                "[GOOGLE_PLAY_VERIFY] productId={}, orderId={}, purchaseState={}, acknowledgementState={}, purchaseTimeMillis={}, tokenPrefix={}",
                request.getProductId(),
                productPurchase.getOrderId(),
                purchaseState,
                productPurchase.getAcknowledgementState(),
                productPurchase.getPurchaseTimeMillis(),
                request.getPurchaseToken() == null
                        ? null
                        : request.getPurchaseToken().substring(0, Math.min(12, request.getPurchaseToken().length()))
        );

        if (purchaseState == null || purchaseState != 0) {
            throw new RuntimeException("Google Play purchase is not completed. state=" + purchaseState);
        }

        boolean acknowledged = productPurchase.getAcknowledgementState() != null
                && productPurchase.getAcknowledgementState() == 1;

        Instant now = Instant.now();

        Payment payment = paymentRepository.save(
                Payment.builder()
                        .user(user)
                        .itemType(PaymentItemType.PACKAGE)
                        .packageEntity(packageEntity)
                        .amount(packageEntity.getPrice())
                        .provider(PaymentProvider.GOOGLE_PLAY)
                        .status(PaymentStatus.APPROVED)
                        .googleProductId(request.getProductId())
                        .googlePurchaseToken(request.getPurchaseToken())
                        .googleOrderId(productPurchase.getOrderId())
                        .googlePurchaseState(productPurchase.getPurchaseState())
                        .googleAcknowledged(acknowledged)
                        .purchasedAt(
                                productPurchase.getPurchaseTimeMillis() != null
                                        ? Instant.ofEpochMilli(productPurchase.getPurchaseTimeMillis())
                                        : now
                        )
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        );

        grantGooglePlayAr30Days(user, payment, packageEntity);

        if (!acknowledged) {
            googlePlayVerifier.acknowledgeProductPurchase(
                    request.getProductId(),
                    request.getPurchaseToken()
            );

            payment.setGoogleAcknowledged(true);
            payment.setUpdatedAt(Instant.now());
            paymentRepository.save(payment);
        }

        return arAccessService.getMyArAccess();
    }


    private void grantGooglePlayAr30Days(User user, Payment payment, Packages packageEntity) {
        Instant now = Instant.now();

        Optional<UserAccess> currentAccessOpt =
                userAccessRepository.findFirstByUserAndAccessTypeAndStatusAndExpiredAtAfterOrderByExpiredAtDesc(
                        user,
                        AccessType.AR_30_DAYS,
                        AccessStatus.ACTIVE,
                        now
                );

        Instant startAt = currentAccessOpt
                .map(UserAccess::getExpiredAt)
                .filter(expiredAt -> expiredAt.isAfter(now))
                .orElse(now);

        Instant expiredAt = startAt.plus(Duration.ofDays(packageEntity.getDurationDays()));

        UserAccess access = UserAccess.builder()
                .user(user)
                .accessType(AccessType.AR_30_DAYS)
                .source(AccessSource.GOOGLE_PLAY_PURCHASE)
                .startAt(startAt)
                .expiredAt(expiredAt)
                .status(AccessStatus.ACTIVE)
                .referenceId(payment.getId())
                .build();

        userAccessRepository.save(access);
    }
}
