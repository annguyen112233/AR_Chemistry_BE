package com.chemistry.demo.dto.request.upload;

import com.chemistry.demo.enums.UploadType;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenerateUploadUrlRequest {
    private String fileName;

    private String contentType;

    private UploadType uploadType;

}
