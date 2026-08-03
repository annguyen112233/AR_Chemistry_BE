package com.chemistry.demo.dto.response.quiz.student;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentPublishedQuizResponse {

    private String quizCode;

    private String quizTitle;

    private String reactionId;

    private String reactionName;

    private String equation;

    private Integer grade;

    private String reactionType;

    private Long questionCount;
}