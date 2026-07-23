package com.chemistry.demo.dto.request.quiz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizImportUploadUrlRequest {

    @NotBlank(message = "reactionCode is required")
    private String reactionCode;

    @NotBlank(message = "contentType is required")
    private String contentType;

    @NotNull(message = "fileSize is required")
    private Long fileSize;
}