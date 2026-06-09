package com.chemistry.demo.entity;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.*;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "chemical_cards",
        indexes = {
                @Index(name = "idx_card_code", columnList = "cardCode"),
                @Index(name = "idx_qr_payload", columnList = "qrPayload"),
                @Index(name = "idx_card_active", columnList = "active"),
                @Index(name = "idx_card_substance", columnList = "substance_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChemicalCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Mã nội bộ của card.
     * Ví dụ: CARD-ZN, CARD-HCL
     */
    @Column(nullable = false, unique = true, length = 100)
    private String cardCode;

    /**
     * Nội dung thật nằm trong QR.
     * Unity scan ra giá trị này.
     * Ví dụ: CHEM-ZN, CHEM-HCL
     */
    @Column(nullable = false, unique = true, length = 255)
    private String qrPayload;

    /**
     * Card này đại diện cho chất nào.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substance_id", nullable = false)
    private ChemicalSubstance substance;

    /**
     * Tên hiển thị trên flash card.
     * Ví dụ: Zn - Kẽm
     */
    @Column(length = 255)
    private String displayName;

    /**
     * Nếu sau này bạn lưu link ảnh thiết kế card/QR.
     */
    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean active;
}
