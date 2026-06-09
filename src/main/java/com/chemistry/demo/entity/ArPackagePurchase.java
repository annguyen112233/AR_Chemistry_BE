package com.chemistry.demo.entity;

import com.chemistry.demo.enums.PaymentProvider;
import com.chemistry.demo.enums.PurchaseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "ar_package_purchases",
        indexes = {
                @Index(name = "idx_ar_package_purchase_user_id", columnList = "user_id"),
                @Index(name = "idx_ar_package_purchase_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArPackagePurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * User mua gói AR.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * DEV_FAKE_PAYMENT hiện tại, GOOGLE_PLAY sau này.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PaymentProvider paymentProvider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PurchaseStatus status;

    /**
     * Giá tại thời điểm mua.
     * Ví dụ: 299000
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kit_activation_code_id", nullable = false)
    private KitActivationCode kitActivationCode;

    /**
     * Thời hạn gói.
     */
    @Builder.Default
    @Column(nullable = false)
    private Integer durationDays = 30;

    /**
     * Google Play product id.
     * Ví dụ: ar_30_days_299k
     */
    @Column(length = 100)
    private String googlePlayProductId;

    /**
     * Sau này tích hợp Google Play mới có.
     */
    @Column(length = 500)
    private String googlePlayPurchaseToken;

    /**
     * Sau này tích hợp Google Play mới có.
     */
    @Column(length = 255)
    private String googlePlayOrderId;

    @Column(nullable = false)
    private Instant purchasedAt;
}
