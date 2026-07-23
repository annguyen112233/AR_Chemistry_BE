package com.chemistry.demo.dto.response.quiz.student;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudentReactionDetailResponse {

    private String reactionId;

    private String reactionName;

    private String equation;

    private String description;

    private Integer quizDuration;

    private String arSceneKey;

    private Integer requiredCardCount;

    private List<RequiredCardResponse> requiredCards;

    @Getter
    @Builder
    public static class RequiredCardResponse {

        private String formula;

        private String cardCode;

        private Integer reactantOrder;

        private Integer coefficient;
    }
}