package com.chemistry.demo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_SERVICE_ACCOUNT_BASE64:}")
    private String firebaseServiceAccountBase64;

    @PostConstruct
    public void initFirebase() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        if (firebaseServiceAccountBase64 == null || firebaseServiceAccountBase64.isBlank()) {
            throw new IllegalStateException("Missing FIREBASE_SERVICE_ACCOUNT_BASE64 environment variable");
        }

        byte[] decodedBytes = Base64.getMimeDecoder()
                .decode(firebaseServiceAccountBase64.trim());

        ByteArrayInputStream serviceAccount =
                new ByteArrayInputStream(decodedBytes);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
    }
}