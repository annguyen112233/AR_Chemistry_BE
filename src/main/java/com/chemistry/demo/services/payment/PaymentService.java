package com.chemistry.demo.services.payment;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.payment.CreatePaymentRequest;
import com.chemistry.demo.dto.response.payment.PaymentResponse;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PageResponse<PaymentResponse> getPaymentsForStaff(Pageable pageable);

    String createPayment(
            CreatePaymentRequest request);

    String approvePayment(String paymentId);
}
