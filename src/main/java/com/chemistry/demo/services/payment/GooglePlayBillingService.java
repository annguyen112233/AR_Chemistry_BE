package com.chemistry.demo.services.payment;

import com.chemistry.demo.dto.request.payment.GooglePlayVerifyRequest;
import com.chemistry.demo.dto.response.reaction.ArAccessResponse;
import com.google.api.services.androidpublisher.model.ProductPurchase;

public interface GooglePlayBillingService {
    ProductPurchase verifyProductPurchase(String productId, String purchaseToken);

    void acknowledgeProductPurchase(String productId, String purchaseToken);}
