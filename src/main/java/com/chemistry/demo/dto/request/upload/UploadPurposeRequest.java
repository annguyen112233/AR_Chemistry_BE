package com.chemistry.demo.dto.request.upload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadPurposeRequest {

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Folder prefix is required")
    private String folderPrefix;

    @NotNull(message = "Max file size is required")
    @Min(value = 1, message = "Max file size must be > 0 bytes")
    private Long maxFileSize;

    @NotBlank(message = "Allowed content types are required")
    private String allowedContentTypes;

    private Boolean active;
}
