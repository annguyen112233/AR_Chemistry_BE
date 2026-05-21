package com.chemistry.demo.services.payment.Impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.feedback.FeedbackListResponse;
import com.chemistry.demo.dto.response.payment.PaymentResponse;
import com.chemistry.demo.entity.Packages;
import com.chemistry.demo.entity.PaymentRequest;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.PaymentStatus;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.PackageErrorCode;
import com.chemistry.demo.mapper.FeedbackMapper;
import com.chemistry.demo.mapper.PaymentMapper;
import com.chemistry.demo.repository.PackageRepository;
import com.chemistry.demo.repository.PaymentRepository;
import com.chemistry.demo.services.payment.PaymentService;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PackageRepository packageRepository;
    private final SecurityUtils securityUtils;
    private final PaymentMapper paymentMapper;
    @Override
    @PreAuthorize("hasAuthority('ROLE_STAFF')")
    public PageResponse<PaymentResponse> getPaymentsForStaff(Pageable pageable) {

        Page<PaymentRequest> payments = paymentRepository.findByStatus(PaymentStatus.PENDING, pageable);

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
    public void createPaymentRequest(String packageId, MultipartFile file) {
        User user = securityUtils.getCurrentUserCognitoSub();
        Packages packageEntity = packageRepository.findById(packageId)
                .orElseThrow(() -> new AppException(PackageErrorCode.PACKAGE_NOT_FOUND));

        PaymentRequest.builder()
                .user(user)
                .packageEntity(packageEntity)
                .amount(packageEntity.getPrice())
                .proofImageUrl(imageUrl)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
