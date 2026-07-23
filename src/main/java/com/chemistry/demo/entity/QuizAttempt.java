package com.chemistry.demo.entity;

import com.chemistry.demo.enums.QuizAttemptStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "quiz_attempts",
        indexes = {
                @Index(
                        name = "idx_attempt_code",
                        columnList = "attempt_code"
                ),
                @Index(
                        name = "idx_attempt_student_quiz_status",
                        columnList = "student_id, quiz_id, status"
                ),
                @Index(
                        name = "idx_attempt_status_expired_at",
                        columnList = "status, expired_at"
                )
        }
)
public class QuizAttempt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(
            name = "attempt_code",
            nullable = false,
            unique = true,
            length = 100
    )
    private String attemptCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "quiz_id",
            nullable = false
    )
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private User student;

    /**
     * Nếu mỗi câu 1 điểm thì score = correctCount.
     */
    @Column(
            name = "score",
            nullable = false
    )
    @Builder.Default
    private Integer score = 0;

    @Column(
            name = "total_questions",
            nullable = false
    )
    @Builder.Default
    private Integer totalQuestions = 0;

    @Column(
            name = "correct_count",
            nullable = false
    )
    @Builder.Default
    private Integer correctCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private QuizAttemptStatus status;

    /**
     * Thời điểm timer bắt đầu.
     * Chỉ có giá trị sau khi AR thành công.
     */
    @Column(name = "started_at")
    private Instant startedAt;

    /**
     * startedAt + quizDurationSeconds.
     */
    @Column(name = "expired_at")
    private Instant expiredAt;

    /**
     * Thời điểm user nộp hoặc hệ thống tự động nộp.
     */
    @Column(name = "submitted_at")
    private Instant submittedAt;

    /**
     * Thời điểm user chủ động thoát giữa chừng.
     */
    @Column(name = "abandoned_at")
    private Instant abandonedAt;

    @Column(
            name = "ar_completed",
            nullable = false
    )
    @Builder.Default
    private Boolean arCompleted = false;

    /**
     * Thời lượng làm quiz tính bằng giây.
     * 420 giây = 7 phút.
     */
    @Column(
            name = "quiz_duration_seconds",
            nullable = false
    )
    @Builder.Default
    private Integer quizDurationSeconds = 420;
}