package com.chemistry.demo.dto.response.upload;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PresignedUploadResponse {

    private Long uploadFileId;

    private String uploadUrl;

    private String fileUrl;

    private String key;

    private String contentType;

    private com.chemistry.demo.enums.UploadType uploadType;

    private Long expiresInSeconds;
}
