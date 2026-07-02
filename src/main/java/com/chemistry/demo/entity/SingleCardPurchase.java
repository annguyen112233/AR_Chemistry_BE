package com.chemistry.demo.entity;

import com.chemistry.demo.enums.PaymentProvider;
import com.chemistry.demo.enums.PurchaseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "single_card_purchases",
        indexes = {
                @Index(name = "idx_single_card_purchase_user_id", columnList = "user_id"),
                @Index(name = "idx_single_card_purchase_card_id", columnList = "single_card_id"),
                @Index(name = "idx_single_card_purchase_status", columnList = "status"),
                @Index(name = "idx_single_card_purchase_provider", columnList = "payment_provider"),
                @Index(name = "idx_single_card_purchase_kp_tx", columnList = "kp_transaction_id")
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
     * Hiện tại chỉ dùng KNOWLEDGE_POINT.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_provider", nullable = false, length = 50)
    private PaymentProvider paymentProvider;

    /**
     * PENDING, PAID, FAILED, CANCELLED...
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PurchaseStatus status;

    /**
     * Giá KP tại thời điểm mua.
     */
    @Column(name = "kp_price", nullable = false)
    private Long kpPrice;

    /**
     * Transaction trừ KP tương ứng.
     */
    @Column(name = "kp_transaction_id", length = 100)
    private String kpTransactionId;

    /**
     * Thời điểm user bấm mua.
     */
    @Column(nullable = false)
    private Instant purchasedAt;

    /**
     * Thời điểm thanh toán KP thành công.
     */
    private Instant paidAt;

    /**
     * Thời điểm quyền AR hết hạn.
     */
    @Column(nullable = false)
    private Instant expiredAt;

    /**
     * Lý do thất bại nếu mua lỗi.
     */
    @Column(length = 1000)
    private String failureReason;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (purchasedAt == null) {
            purchasedAt = now;
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}