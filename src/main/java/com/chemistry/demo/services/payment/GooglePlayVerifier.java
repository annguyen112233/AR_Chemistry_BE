package com.chemistry.demo.services.payment;

import com.google.api.services.androidpublisher.model.ProductPurchase;

public interface GooglePlayVerifier {
    ProductPurchase verifyProductPurchase(String productId, String purchaseToken);

    void acknowledgeProductPurchase(String productId, String purchaseToken);
}