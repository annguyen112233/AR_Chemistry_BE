package com.chemistry.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "single_cards",
        indexes = {
                @Index(name = "idx_single_card_code", columnList = "code"),
                @Index(name = "idx_single_card_active", columnList = "active"),
                @Index(name = "idx_single_card_substance_id", columnList = "substance_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Mã card trong hệ thống.
     * Ví dụ: CARD_ZN, CARD_HCL
     */
    @Column(nullable = false, unique = true, length = 100)
    private String code;

    /**
     * Tên hiển thị ngoài shop.
     * Ví dụ: Card AR Zinc 30 ngày
     */
    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 1000)
    private String description;

    /**
     * Giá bán card digital.
     * Ví dụ: 15000
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /**
     * Thời hạn sử dụng sau khi mua.
     */
    @Builder.Default
    @Column(nullable = false)
    private Integer durationDays = 30;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = false;

    /**
     * Một card digital mở AR cho một chất cụ thể.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substance_id", nullable = false, unique = true)
    private ChemicalSubstance substance;

    @Column(nullable = false, unique = true, length = 255)
    private String qrContent;

    @Column(name = "qr_s3_key", nullable = false, length = 500)
    private String qrS3Key;

    /**
     * Product ID trên Google Play.
     * Hiện tại fake payment vẫn có thể lưu trước.
     * Ví dụ: single_card_15k
     */
    @Column(nullable = false, length = 100)
    private String googlePlayProductId;
}
