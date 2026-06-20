package com.chemistry.demo.services.payment.Impl;

import com.chemistry.demo.dto.request.payment.GooglePlayVerifyRequest;
import com.chemistry.demo.dto.response.reaction.ArAccessResponse;
import com.chemistry.demo.entity.Packages;
import com.chemistry.demo.entity.Payment;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.entity.UserAccess;
import com.chemistry.demo.enums.*;
import com.chemistry.demo.repository.PackageRepository;
import com.chemistry.demo.services.payment.GooglePlayBillingService;
import com.chemistry.demo.utils.SecurityUtils;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;
import com.google.api.services.androidpublisher.model.ProductPurchase;
import com.google.api.services.androidpublisher.model.ProductPurchasesAcknowledgeRequest;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GooglePlayBillingServiceImpl implements GooglePlayBillingService {
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
