package com.chemistry.demo.dto.quizCSV.staff;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
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

    private String lessonCode;
    private String lessonTitle;

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