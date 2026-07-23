package com.chemistry.demo.dto.response.quiz.staff;

import com.chemistry.demo.enums.QuizStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffReactionQuizOverviewResponse {

    private String reactionId;

    private String reactionCode;

    private String reactionName;

    private String equation;

    private Integer grade;

    private String reactionCategory;

    private String reactionType;

    private Boolean active;

    private Boolean hasQuiz;

    private String latestQuizCode;

    private String latestQuizTitle;

    private QuizStatus latestQuizStatus;

    private Integer latestQuizVersion;

    private Long questionCount;
}