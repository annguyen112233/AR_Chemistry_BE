package com.chemistry.demo.services.payment;

import com.chemistry.demo.dto.request.payment.GooglePlayVerifyRequest;
import com.chemistry.demo.dto.response.reaction.ArAccessResponse;

public interface GooglePlayBillingService {
    ArAccessResponse verify(GooglePlayVerifyRequest request);
}
