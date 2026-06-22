package com.chemistry.demo.services.payment.Impl;

import com.chemistry.demo.services.payment.GooglePlayVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;
import com.google.api.services.androidpublisher.model.ProductPurchase;
import com.google.api.services.androidpublisher.model.ProductPurchasesAcknowledgeRequest;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.List;

@Service
public class GooglePlayVerifierImpl implements GooglePlayVerifier {

    @Value("${google.play.package-name}")
    private String packageName;

    @Value("${google.play.service-account-path}")
    private String serviceAccountPath;

    private AndroidPublisher androidPublisher() {
        try {
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new FileInputStream(serviceAccountPath))
                    .createScoped(List.of(AndroidPublisherScopes.ANDROIDPUBLISHER));

            return new AndroidPublisher.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            )
                    .setApplicationName("LAB EDU Backend")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create Google Play client", e);
        }
    }

    @Override
    public ProductPurchase verifyProductPurchase(String productId, String purchaseToken) {
        try {
            return androidPublisher()
                    .purchases()
                    .products()
                    .get(packageName, productId, purchaseToken)
                    .execute();

        } catch (Exception e) {
            throw new RuntimeException("Failed to verify Google Play purchase", e);
        }
    }

    @Override
    public void acknowledgeProductPurchase(String productId, String purchaseToken) {
        try {
            ProductPurchasesAcknowledgeRequest request =
                    new ProductPurchasesAcknowledgeRequest();

            androidPublisher()
                    .purchases()
                    .products()
                    .acknowledge(packageName, productId, purchaseToken, request)
                    .execute();

        } catch (Exception e) {
            throw new RuntimeException("Failed to acknowledge Google Play purchase", e);
        }
    }
}