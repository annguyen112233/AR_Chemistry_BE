package com.chemistry.demo.controller.payment;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.response.payment.FakePurchaseResponse;
import com.chemistry.demo.services.payment.DevPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev/fake-purchase")
@RequiredArgsConstructor
public class DevPaymentController {

    private final DevPaymentService devPaymentService;

    @PostMapping("/ar-30-days")
    public ApiResponse<FakePurchaseResponse> fakePurchaseAr30Days() {
        return ApiResponse.<FakePurchaseResponse>builder()
                .success(true)
                .data(devPaymentService.fakePurchaseAr30Days())
                .build();
    }
}