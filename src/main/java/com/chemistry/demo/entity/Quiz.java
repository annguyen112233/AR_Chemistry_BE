package com.chemistry.demo.entity;

import com.chemistry.demo.enums.QuizStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "quizzes",
        indexes = {
                @Index(
                        name = "idx_quiz_code",
                        columnList = "quiz_code"
                ),
                @Index(
                        name = "idx_quiz_reaction_status",
                        columnList = "reaction_id, status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(
            name = "quiz_code",
            unique = true,
            nullable = false,
            length = 100
    )
    private String quizCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "reaction_id",
            nullable = false
    )
    private ReactionDefinition reaction;

    @Column(
            name = "title",
            nullable = false,
            length = 255
    )
    private String title;

    /**
     * AI hoặc MANUAL.
     * Có thể chuyển thành enum sau.
     */
    @Column(
            name = "generated_by",
            length = 30
    )
    private String generatedBy;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private QuizStatus status;

    @Column(
            name = "version",
            nullable = false
    )
    @Builder.Default
    private Integer version = 1;


    @Column(name = "question_limit", nullable = false)
    private Integer questionLimit;

    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;

    @Column(name = "import_job_code")
    private String importJobCode;
}