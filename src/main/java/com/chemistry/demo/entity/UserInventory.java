package com.chemistry.demo.entity;

import com.chemistry.demo.enums.InventorySource;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_inventories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_substance",
                        columnNames = {"user_id", "substance_id"}
                )
        },
        indexes = {
                @Index(name = "idx_inventory_user", columnList = "user_id"),
                @Index(name = "idx_inventory_substance", columnList = "substance_id"),
                @Index(name = "idx_inventory_active", columnList = "active")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substance_id", nullable = false)
    private ChemicalSubstance substance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private InventorySource source;

    /**
     * Ví dụ:
     * source = KIT_ACTIVATION
     * sourceRef = activationCodeId hoặc code
     */
    @Column(length = 255)
    private String sourceRef;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Boolean active;

    private Instant acquiredAt;

    private Instant expiresAt;
}
