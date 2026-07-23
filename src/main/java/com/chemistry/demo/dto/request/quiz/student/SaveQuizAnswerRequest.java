package com.chemistry.demo.dto.request.quiz.student;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveQuizAnswerRequest {

    @NotBlank(message = "Answer must not be blank")
    private String answer;
}