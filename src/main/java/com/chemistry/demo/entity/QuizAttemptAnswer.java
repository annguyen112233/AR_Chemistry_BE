package com.chemistry.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "quiz_attempt_answers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_attempt_question",
                        columnNames = {
                                "attempt_id",
                                "question_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_attempt_answer_attempt",
                        columnList = "attempt_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "attempt_id",
            nullable = false
    )
    private QuizAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "question_id",
            nullable = false
    )
    private QuizQuestion question;

    /**
     * Đáp án user đang chọn.
     * Có thể null hoặc rỗng nếu chưa trả lời.
     */
    @Column(
            name = "student_answer",
            length = 1000
    )
    private String studentAnswer;

    /**
     * Snapshot đáp án đúng tại lúc chấm.
     */
    @Column(
            name = "correct_answer",
            length = 1000
    )
    private String correctAnswer;

    /**
     * Null khi chưa nộp bài.
     * true/false sau khi đã chấm.
     */
    @Column(name = "is_correct")
    private Boolean correct;

    /**
     * Snapshot giải thích tại lúc chấm.
     */
    @Column(
            name = "explanation",
            columnDefinition = "TEXT"
    )
    private String explanation;

    /**
     * Thời điểm student chọn hoặc cập nhật đáp án.
     */
    @Column(name = "answered_at")
    private Instant answeredAt;

    /**
     * Thời điểm backend chấm đáp án.
     */
    @Column(name = "graded_at")
    private Instant gradedAt;
}