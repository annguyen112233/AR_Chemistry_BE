package com.chemistry.demo.dto.response.quiz.student;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentReactionResponse {

    private String reactionId;

    private String reactionCode;

    private String reactionName;

    private String equation;

    private Integer grade;

    private String reactionCategory;

    private Boolean completed;

    private Boolean hasRunningAttempt;

    private String activeAttemptCode;

    private Long remainingSeconds;

    private String latestCompletedAttemptCode;

    private Boolean canStart;

    private Boolean canContinue;

    private Boolean canViewHistory;

    private Boolean canRetry;
}