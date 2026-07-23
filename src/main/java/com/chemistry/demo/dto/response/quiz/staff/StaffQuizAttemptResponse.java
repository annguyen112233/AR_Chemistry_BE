package com.chemistry.demo.dto.response.quiz.staff;

import com.chemistry.demo.enums.QuizAttemptStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class StaffQuizAttemptResponse {

    private String attemptCode;

    private String studentId;
    private String studentName;
    private String studentEmail;

    private String quizCode;
    private String quizTitle;

    private String reactionId;
    private String reactionCode;
    private String reactionName;
    private String equation;
    private Integer grade;
    private String reactionCategory;

    private Double score;
    private Integer totalQuestions;
    private Integer correctCount;

    private QuizAttemptStatus status;

    private Instant startedAt;
    private Instant expiredAt;
    private Instant submittedAt;
}