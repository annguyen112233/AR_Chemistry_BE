package com.chemistry.demo.controller.payment;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.payment.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/payment")
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
}
