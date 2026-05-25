package com.chemistry.demo.services.payment.Impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.payment.CreatePaymentRequest;
import com.chemistry.demo.dto.response.payment.PaymentResponse;
import com.chemistry.demo.entity.*;
import com.chemistry.demo.enums.PaymentStatus;
import com.chemistry.demo.exception.*;
import com.chemistry.demo.mapper.PaymentMapper;
import com.chemistry.demo.repository.*;
import com.chemistry.demo.services.payment.PaymentService;
import com.chemistry.demo.services.redis.RedisFeatureService;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PackageRepository packageRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ChemicalCardRepository chemicalCardRepository;
    private final CardBundleRepository cardBundleRepository;
    private final RedisFeatureService redisFeatureService;
    private final SecurityUtils securityUtils;
    private final PaymentMapper paymentMapper;

    @Override
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public PageResponse<PaymentResponse> getPaymentsForStaff(Pageable pageable) {
        Page<Payment> payments = paymentRepository.findAll(pageable);

        List<PaymentResponse> items = payments.getContent()
                .stream()
                .map(paymentMapper::toPaymentResponse)
                .toList();

        return PageResponse.<PaymentResponse>builder()
                .items(items)
                .page(payments.getNumber())
                .size(payments.getSize())
                .totalItems(payments.getTotalElements())
                .totalPages(payments.getTotalPages())
                .first(payments.isFirst())
                .last(payments.isLast())
                .hasNext(payments.hasNext())
                .hasPrevious(payments.hasPrevious())
                .build();

    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public String createPayment(CreatePaymentRequest request) {
        User user = securityUtils.getCurrentUserCognitoSub();

        if (request.getProofImageUrl() == null || request.getProofImageUrl().isBlank()) {
            throw new AppException(PaymentErrorCode.INVALID_PROOF_IMAGE);
        }

        if (paymentRepository.existsByUserAndStatus(user, PaymentStatus.PENDING)) {
            throw new AppException(PaymentErrorCode.PAYMENT_ALREADY_PENDING);
        }

        Payment.PaymentBuilder paymentBuilder = Payment.builder()
                .user(user)
                .itemType(request.getItemType())
                .proofImageUrl(request.getProofImageUrl())
                .status(PaymentStatus.PENDING)
                .createdAt(Instant.now());

        switch (request.getItemType()) {
            case PACKAGE -> {
                Packages packageEntity = packageRepository.findById(request.getItemId())
                        .orElseThrow(() -> new AppException(PackageErrorCode.PACKAGE_NOT_FOUND));

                paymentBuilder
                        .packageEntity(packageEntity)
                        .chemicalCard(null)
                        .amount(packageEntity.getPrice());
            }

            case CHEMICAL_CARD -> {
                ChemicalCard chemicalCard = chemicalCardRepository.findById(request.getItemId())
                        .orElseThrow(() -> new AppException(ChemicalCardErrorCode.CHEMICAL_CARD_NOT_FOUND));

                if (!Boolean.TRUE.equals(chemicalCard.getActive())
                        || !Boolean.TRUE.equals(chemicalCard.getPurchasable())) {
                    throw new AppException(ChemicalCardErrorCode.CHEMICAL_CARD_NOT_PURCHASABLE);
                }

                paymentBuilder
                        .packageEntity(null)
                        .chemicalCard(chemicalCard)
                        .amount(chemicalCard.getPrice());
            }

            case CARD_BUNDLE -> {
                CardBundle bundle = cardBundleRepository.findById(request.getItemId())
                        .orElseThrow(() -> new AppException(CardBundleErrorCode.CARD_BUNDLE_NOT_FOUND));

                if (!Boolean.TRUE.equals(bundle.getActive())
                        || !Boolean.TRUE.equals(bundle.getPurchasable())) {
                    throw new AppException(CardBundleErrorCode.CARD_BUNDLE_NOT_PURCHASABLE);
                }

                paymentBuilder
                        .packageEntity(null)
                        .chemicalCard(null)
                        .cardBundle(bundle)
                        .amount(bundle.getDiscountedPrice());
            }

            default -> throw new AppException(PaymentErrorCode.INVALID_PAYMENT_ITEM_TYPE);
        }

        Payment payment = paymentBuilder.build();

        paymentRepository.save(payment);

        return "Payment created successfully";
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    @Transactional
    public String approvePayment(String paymentId) {

        User staff = securityUtils.getCurrentUserCognitoSub();

        Payment payment = paymentRepository
                .findByIdAndStatus(
                        paymentId,
                        PaymentStatus.PENDING
                )
                .orElseThrow(() ->
                        new AppException(
                                PaymentErrorCode.PAYMENT_NOT_FOUND
                        )
                );

        User student = payment.getUser();

        subscriptionRepository
                .deactivateAllByUser(student);

        Instant now = Instant.now();

        Instant expiredAt = now.plus(
                payment.getPackageEntity()
                        .getDurationDays(),
                ChronoUnit.DAYS
        );

        Subscriptions subscription = Subscriptions.builder()
                .user(student)
                .packageEntity(payment.getPackageEntity())
                .startDate(now)
                .endDate(expiredAt)
                .active(true)
                .build();

        subscriptionRepository.save(subscription);

        payment.setStatus(PaymentStatus.APPROVED);

        payment.setApprovedBy(staff);

        payment.setApprovedAt(now);

        paymentRepository.save(payment);

        redisFeatureService.cacheUserFeatures(student);

        return "Payment approved successfully";
    }


}
