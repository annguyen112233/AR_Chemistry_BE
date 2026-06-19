package com.chemistry.demo.entity;

import com.chemistry.demo.enums.PaymentItemType;
import com.chemistry.demo.enums.PaymentProvider;
import com.chemistry.demo.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payment_user", columnList = "user_id"),
                @Index(name = "idx_payment_status", columnList = "status"),
                @Index(name = "idx_payment_provider", columnList = "provider"),
                @Index(name = "idx_payment_google_product_id", columnList = "googleProductId")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payment_google_purchase_token",
                        columnNames = "googlePurchaseToken"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentItemType itemType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Packages packageEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChemicalCard chemicalCard;

    @ManyToOne(fetch = FetchType.LAZY)
    private CardBundle cardBundle;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // Manual payment fields
    private String proofImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private User approvedBy;

    private Instant approvedAt;

    // Google Play Billing fields
    private String googleProductId;

    @Column(columnDefinition = "TEXT")
    private String googlePurchaseToken;

    private String googleOrderId;

    private Boolean googleAcknowledged;

    private Integer googlePurchaseState;

    private Instant purchasedAt;

    private Instant expiresAt;

    @Column(columnDefinition = "TEXT")
    private String rawProviderResponse;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (provider == null) {
            provider = PaymentProvider.MANUAL;
        }

        if (status == null) {
            status = PaymentStatus.PENDING;
        }

        if (googleAcknowledged == null) {
            googleAcknowledged = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}