package com.chemistry.demo.dto.request.quiz;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizImportUploadUrlRequest {
    private String lessonCode;
    private String contentType;
    private Long fileSize;
}