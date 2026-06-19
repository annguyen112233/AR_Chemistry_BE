package com.chemistry.demo.controller.payment;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.request.payment.GooglePlayVerifyRequest;
import com.chemistry.demo.dto.response.reaction.ArAccessResponse;
import com.chemistry.demo.services.payment.GooglePlayBillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/billing/google-play")
@RequiredArgsConstructor
public class GooglePlayBillingController {

    private final GooglePlayBillingService googlePlayBillingService;

    @PostMapping("/verify")
    public ApiResponse<ArAccessResponse> verify(@RequestBody GooglePlayVerifyRequest request) {
        return ApiResponse.<ArAccessResponse>ok()
                .data(googlePlayBillingService.verify(request))
                .build();
    }
}