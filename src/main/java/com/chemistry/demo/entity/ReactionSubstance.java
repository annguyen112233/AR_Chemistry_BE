package com.chemistry.demo.entity;

import com.chemistry.demo.entity.BaseEntity;
import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.entity.ReactionDefinition;
import com.chemistry.demo.enums.ReactionRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "reaction_substances",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reaction_role_order",
                        columnNames = {
                                "reaction_id",
                                "role",
                                "substance_order"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionSubstance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "reaction_id",
            nullable = false
    )
    private ReactionDefinition reaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "substance_id",
            nullable = false
    )
    private ChemicalSubstance substance;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "role",
            nullable = false,
            length = 20
    )
    private ReactionRole role;

    @Column(
            name = "coefficient",
            nullable = false
    )
    private Integer coefficient;

    @Column(
            name = "substance_order",
            nullable = false
    )
    private Integer substanceOrder;
}