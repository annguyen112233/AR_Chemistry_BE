package com.chemistry.demo.services.payment;

import com.chemistry.demo.dto.response.payment.FakePurchaseResponse;

public interface DevPaymentService {
    FakePurchaseResponse fakePurchaseAr30Days();
}
