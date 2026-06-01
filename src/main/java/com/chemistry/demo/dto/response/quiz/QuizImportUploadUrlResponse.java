package com.chemistry.demo.dto.response.quiz;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizImportUploadUrlResponse {
    private String key;
    private String uploadUrl;
    private String fileUrl;
}
