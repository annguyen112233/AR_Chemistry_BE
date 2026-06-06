package com.chemistry.demo.entity;

import com.chemistry.demo.enums.ArSceneKey;
import com.chemistry.demo.enums.ReactionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "reaction_definitions",
        indexes = {
                @Index(name = "idx_reaction_code", columnList = "code"),
                @Index(name = "idx_reaction_active", columnList = "active")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 500)
    private String equation;

    /**
     * Ví dụ:
     * METAL_ACID
     * PRECIPITATION
     * NEUTRALIZATION
     * THERMAL_DECOMPOSITION
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private ReactionType reactionType;

    /**
     * Unity dùng field này để chọn template AR.
     * Ví dụ:
     * METAL_ACID_GAS
     * PRECIPITATION
     * THERMAL_DECOMPOSITION_GAS
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private ArSceneKey arSceneKey;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Boolean active;
}
