package com.chemistry.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "kit_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_kit_substance",
                        columnNames = {"kit_id", "substance_id"}
                )
        },
        indexes = {
                @Index(name = "idx_kit_item_kit", columnList = "kit_id"),
                @Index(name = "idx_kit_item_substance", columnList = "substance_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KitItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kit_id", nullable = false)
    private Kit kit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substance_id", nullable = false)
    private ChemicalSubstance substance;

    /**
     * Nếu sau này muốn tính số lượng card/chai/lọ trong hộp.
     * Hiện tại có thể để 1.
     */
    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Boolean active;
}