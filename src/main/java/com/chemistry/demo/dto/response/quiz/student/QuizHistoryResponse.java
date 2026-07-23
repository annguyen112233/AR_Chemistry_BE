package com.chemistry.demo.dto.response.quiz.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizHistoryResponse {

    private String attemptCode;

    private String reactionName;

    private String equation;

    private Integer score;

    private Integer totalQuestions;

    private Integer correctCount;

    private Instant submittedAt;

}