package com.chemistry.demo.dto.request.quiz.staff;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateReactionLearningContentRequest {

    @NotBlank
    private String script;
}