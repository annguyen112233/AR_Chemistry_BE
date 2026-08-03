package com.chemistry.demo.entity;

import com.chemistry.demo.enums.QuizQuestionStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "quiz_questions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_quiz_question_order",
                        columnNames = {
                                "quiz_id",
                                "question_order"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_question_quiz_status",
                        columnList = "quiz_id, status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "quiz_id",
            nullable = false
    )
    private Quiz quiz;

    @Column(
            name = "question_text",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String questionText;

    @Column(
            name = "question_order",
            nullable = false
    )
    private Integer questionOrder;

    /**
     * Ví dụ A, B, C hoặc D.
     */
    @Column(
            name = "correct_answer",
            nullable = false,
            length = 1000
    )
    private String correctAnswer;

    @Column(
            name = "explanation",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String explanation;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private QuizQuestionStatus status =
            QuizQuestionStatus.ACTIVE;
}