package com.chemistry.demo.dto.request.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GooglePlayVerifyRequest {
    private String productId;
    private String purchaseToken;
}