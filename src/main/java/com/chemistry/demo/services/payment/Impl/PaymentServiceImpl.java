package com.chemistry.demo.services.payment.Impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.payment.createPaymentRequest;
import com.chemistry.demo.dto.response.payment.PaymentResponse;
import com.chemistry.demo.entity.Packages;
import com.chemistry.demo.entity.Payment;
import com.chemistry.demo.entity.Subscriptions;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.PaymentStatus;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.PackageErrorCode;
import com.chemistry.demo.exception.PaymentErrorCode;
import com.chemistry.demo.mapper.PaymentMapper;
import com.chemistry.demo.repository.PackageRepository;
import com.chemistry.demo.repository.PaymentRepository;
import com.chemistry.demo.repository.SubscriptionRepository;
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
    public String createPayment(createPaymentRequest request) {
        User user = securityUtils.getCurrentUserCognitoSub();
        Packages packageEntity = packageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new AppException(PackageErrorCode.PACKAGE_NOT_FOUND));

        if (request.getProofImageUrl() == null || request.getProofImageUrl().isBlank()) {
            throw new AppException(
                    PaymentErrorCode.INVALID_PROOF_IMAGE
            );
        }

        if(paymentRepository.existsByUserAndPackageEntityAndStatus(
                user,
                packageEntity,
                PaymentStatus.PENDING
        )) {
            throw new AppException(
                    PaymentErrorCode.PAYMENT_ALREADY_PENDING
            );
        }

        if(paymentRepository.existsByUserAndStatus(
                user,
                PaymentStatus.PENDING
        )) {
            throw new AppException(
                    PaymentErrorCode.PAYMENT_ALREADY_PENDING
            );
        }

        Payment payment = Payment.builder()
                .user(user)
                .packageEntity(packageEntity)
                .amount(packageEntity.getPrice())
                .proofImageUrl(request.getProofImageUrl())
                .status(PaymentStatus.PENDING)
                .createdAt(Instant.now())
                .build();

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
