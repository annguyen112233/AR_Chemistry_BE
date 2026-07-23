package com.chemistry.demo.dto.response.quiz.staff;

import com.chemistry.demo.enums.QuizAttemptStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class StaffQuizAttemptDetailResponse {

    private String attemptCode;

    private String studentId;
    private String studentName;
    private String studentEmail;

    private String quizCode;
    private String quizTitle;

    /*
     * Thông tin phản ứng thay cho Lesson.
     */
    private String reactionId;
    private String reactionCode;
    private String reactionName;
    private String equation;
    private Integer grade;
    private String reactionCategory;

    private Integer score;
    private Integer totalQuestions;
    private Integer correctCount;

    private QuizAttemptStatus status;

    /*
     * Thời gian attempt.
     */
    private Instant startedAt;
    private Instant expiredAt;
    private Instant submittedAt;

    private List<AnswerDetail> answers;

    @Getter
    @Builder
    public static class AnswerDetail {

        private String questionId;

        private Integer questionOrder;

        private String questionText;

        /*
         * A, B, C hoặc D.
         */
        private String studentAnswer;

        /*
         * Đáp án đúng được snapshot khi chấm bài.
         */
        private String correctAnswer;

        private Boolean correct;

        private String explanation;

        private Instant answeredAt;
    }
}