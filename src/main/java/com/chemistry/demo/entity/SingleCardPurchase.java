package com.chemistry.demo.entity;

import com.chemistry.demo.enums.PaymentProvider;
import com.chemistry.demo.enums.PurchaseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "single_card_purchases",
        indexes = {
                @Index(name = "idx_single_card_purchase_user_id", columnList = "user_id"),
                @Index(name = "idx_single_card_purchase_card_id", columnList = "single_card_id"),
                @Index(name = "idx_single_card_purchase_status", columnList = "status"),
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleCardPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * User mua card.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Card digital được mua.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "single_card_id", nullable = false)
    private SingleCard singleCard;

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
     * Nên lưu lại để sau này dù SingleCard đổi giá,
     * lịch sử mua vẫn đúng.
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;


    /**
     * Google Play product id.
     * Fake payment vẫn lưu để sau này flow giống Google Play hơn.
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