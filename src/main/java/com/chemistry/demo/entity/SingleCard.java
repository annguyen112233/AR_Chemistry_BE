package com.chemistry.demo.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 1000)
    private String description;

    /**
     * Giá bán card digital bằng Knowledge Point.
     * Ví dụ: 1500 KP
     */
    @Column(nullable = false)
    private Long kpPrice;

    @Builder.Default
    @Column(nullable = false)
    private Integer durationDays = 30;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substance_id", nullable = false, unique = true)
    private ChemicalSubstance substance;

    @Column(nullable = false, unique = true, length = 255)
    private String qrContent;

    @Column(name = "qr_s3_key", nullable = false, length = 500)
    private String qrS3Key;
}