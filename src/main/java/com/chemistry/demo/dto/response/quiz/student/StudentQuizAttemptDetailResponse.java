package com.chemistry.demo.dto.response.quiz.student;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentQuizAttemptDetailResponse {

    private String attemptCode;

    private String quizCode;
    private String quizTitle;

    // Thay lesson
    private String reactionId;
    private String reactionName;
    private String equation;
    private String description;

    private Integer grade;
    private String reactionType;

    private Integer score;
    private Integer totalQuestions;
    private Integer correctCount;

    private String status;
    private Instant submittedAt;

    private List<AnswerDetail> answers;

    @Getter
    @Builder
    public static class AnswerDetail {

        private String questionId;

        private Integer questionOrder;

        private String questionText;

        private String studentAnswer;

        private String correctAnswer;

        private Boolean correct;

        private String explanation;
    }
}