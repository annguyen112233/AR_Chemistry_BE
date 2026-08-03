package com.chemistry.demo.dto.response.quiz.student;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentQuizAttemptHistoryResponse {

    private String attemptCode;

    private String quizCode;
    private String quizTitle;

    private String lessonCode;
    private String lessonTitle;

    private Integer score;
    private Integer totalQuestions;
    private Integer correctCount;

    private String status;
    private Instant submittedAt;
}