package com.chemistry.demo.services.payment;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.payment.PaymentResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface PaymentService {
    PageResponse<PaymentResponse> getPaymentsForStaff(Pageable pageable);
    void createPaymentRequest(
            String packageId,
            MultipartFile file
    );
}
