package com.chemistry.demo.dto.response.upload;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadPurposeResponse {

    private Long id;
    private String code;
    private String folderPrefix;
    private Long maxFileSize;
    private String allowedContentTypes;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
