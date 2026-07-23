package com.chemistry.demo.dto.request.quiz;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartQuizImportRequest {

    private String reactionCode;

    private String s3Key;

    private String originalFilename;
}