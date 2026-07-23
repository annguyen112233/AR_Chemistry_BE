package com.chemistry.demo.dto.response.quiz.staff;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizPromptResponse {

    private String reactionCode;

    private String reactionName;

    private String prompt;
}