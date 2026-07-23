package com.chemistry.demo.dto.response.quiz.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentQuizResponse {

    private String attemptCode;

    private String reactionName;

    private String equation;

    private String description;

    private Integer remainingSeconds;

    private List<StudentQuizQuestionResponse> questions;

}