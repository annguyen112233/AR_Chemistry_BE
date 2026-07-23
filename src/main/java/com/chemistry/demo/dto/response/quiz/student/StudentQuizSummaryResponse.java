package com.chemistry.demo.dto.response.quiz.student;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentQuizSummaryResponse {

    private String quizCode;

    private String reactionId;

    private String reactionName;

    private String equation;

    private Integer grade;

    private String reactionType;

    private Long questionCount;
}
