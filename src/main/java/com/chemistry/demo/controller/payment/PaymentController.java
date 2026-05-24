package com.chemistry.demo.controller.payment;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.payment.CreatePaymentRequest;
import com.chemistry.demo.dto.response.payment.PaymentResponse;
import com.chemistry.demo.services.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping
    public ApiResponse<PageResponse<PaymentResponse>> getPaymentsForStaff(@PageableDefault(
            size = 10,
            sort = "createdAt",
            direction = Sort.Direction.DESC

    ) Pageable pageable) {
        return ApiResponse.<PageResponse<PaymentResponse>>ok()
                .data(paymentService.getPaymentsForStaff(pageable))
                .build();
    }

    @PostMapping
    public ApiResponse<String> createPayment(@RequestBody CreatePaymentRequest request) {
        String payment = paymentService.createPayment(request);
        return ApiResponse.<String>ok()
                .data(payment)
                .build();
    }

    @PostMapping("/approve/{paymentId}")
    public ApiResponse<String> approvePayment(@PathVariable String paymentId) {
        String response = paymentService.approvePayment(paymentId);
        return ApiResponse.<String>ok()
                .data(response)
                .build();
    }


}
