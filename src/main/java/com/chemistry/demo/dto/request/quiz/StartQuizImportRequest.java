package com.chemistry.demo.dto.request.quiz;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartQuizImportRequest {
    private String lessonCode;
    private String s3Key;
    private String originalFilename;
}
