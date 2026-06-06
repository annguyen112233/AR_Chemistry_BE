package com.chemistry.demo.entity;

import com.chemistry.demo.enums.ReactionRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "reaction_substances",
        indexes = {
                @Index(name = "idx_reaction_substance_reaction", columnList = "reaction_id"),
                @Index(name = "idx_reaction_substance_substance", columnList = "substance_id"),
                @Index(name = "idx_reaction_substance_role", columnList = "role")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionSubstance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reaction_id", nullable = false)
    private ReactionDefinition reaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substance_id", nullable = false)
    private ChemicalSubstance substance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReactionRole role;

    @Column(nullable = false)
    private Integer coefficient;
}
