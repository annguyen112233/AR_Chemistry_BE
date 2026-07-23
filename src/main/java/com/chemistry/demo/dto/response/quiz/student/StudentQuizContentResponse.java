package com.chemistry.demo.dto.response.quiz.student;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudentQuizContentResponse {

    private String attemptCode;

    private String reactionName;

    private String equation;

    private String script;

    private Long remainingSeconds;

    private List<QuestionResponse> questions;

    @Getter
    @Builder
    public static class QuestionResponse {

        private String questionId;

        private Integer questionOrder;

        private String questionText;

        private String selectedAnswer;

        private List<OptionResponse> options;
    }

    @Getter
    @Builder
    public static class OptionResponse {

        private String optionKey;

        private String optionText;

        private Integer optionOrder;
    }
}