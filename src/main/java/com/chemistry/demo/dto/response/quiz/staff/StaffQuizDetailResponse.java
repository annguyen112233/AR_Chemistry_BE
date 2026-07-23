package com.chemistry.demo.dto.response.quiz.staff;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.enums.QuizStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffQuizDetailResponse {

    private String quizCode;

    private String reactionId;

    private String reactionCode;

    private String reactionName;

    private String equation;

    private Integer grade;

    private String reactionCategory;

    private String title;

    private QuizStatus status;

    private String generatedBy;

    private Integer version;

    private Integer questionLimit;

    private Integer durationSeconds;

    private PageResponse<StaffQuizQuestionResponse> questions;
}