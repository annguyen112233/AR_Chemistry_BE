package com.chemistry.demo.dto.response.upload;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PresignedUploadResponse {

    private String uploadUrl;

    private String fileUrl;
}
