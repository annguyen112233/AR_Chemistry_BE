package com.chemistry.demo.dto.response.quiz.staff;

import com.chemistry.demo.enums.QuizQuestionStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StaffQuizQuestionResponse {

    private String id;

    private Integer questionOrder;

    private String questionText;

    private List<StaffQuizOptionResponse> options;

    private String correctAnswer;

    private String explanation;

    private QuizQuestionStatus status;
}