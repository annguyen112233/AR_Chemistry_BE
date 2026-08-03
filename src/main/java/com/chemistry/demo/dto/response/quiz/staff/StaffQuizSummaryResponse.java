package com.chemistry.demo.dto.response.quiz.staff;

import com.chemistry.demo.enums.QuizStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffQuizSummaryResponse {

    private String quizCode;

    private String reactionId;

    private String reactionCode;

    private String reactionName;

    private String title;

    private QuizStatus status;

    private String generatedBy;

    private Integer version;

    private Integer durationSeconds;

    private Integer questionLimit;

    private Long questionCount;
}