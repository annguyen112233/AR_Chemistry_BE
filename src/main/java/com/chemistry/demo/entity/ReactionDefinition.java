package com.chemistry.demo.entity;

import com.chemistry.demo.enums.ArSceneKey;
import com.chemistry.demo.enums.ReactionCategory;
import com.chemistry.demo.enums.ReactionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "reaction_definitions",
        indexes = {
                @Index(
                        name = "idx_reaction_code",
                        columnList = "code"
                ),
                @Index(
                        name = "idx_reaction_active",
                        columnList = "active"
                ),
                @Index(
                        name = "idx_reaction_grade_category_active",
                        columnList = "grade, reaction_category, active"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionDefinition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(
            name = "code",
            nullable = false,
            unique = true,
            length = 100
    )
    private String code;

    @Column(
            name = "name",
            nullable = false,
            length = 255
    )
    private String name;

    @Column(
            name = "equation",
            nullable = false,
            length = 500
    )
    private String equation;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "reaction_category",
            nullable = false,
            length = 30
    )
    private ReactionCategory reactionCategory;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "reaction_type",
            nullable = false,
            length = 100
    )
    private ReactionType reactionType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "ar_scene_key",
            nullable = false,
            length = 100
    )
    private ArSceneKey arSceneKey;

    @Column(
            name = "description",
            length = 1000
    )
    private String description;

    @Column(
            name = "reaction_script",
            columnDefinition = "TEXT"
    )
    private String script;

    @Column(
            name = "grade",
            nullable = false
    )
    private Integer grade;

    @Column(
            name = "active",
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;
}