package com.chemistry.demo.controller.feedback;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.request.upload.GenerateUploadUrlRequest;
import com.chemistry.demo.dto.response.upload.PresignedUploadResponse;
import com.chemistry.demo.services.aws.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileUploadController {
    private final FileService fileService;

    @PostMapping("/presigned-url")
    public ApiResponse<PresignedUploadResponse> generatePresignedUrl(
            @RequestBody GenerateUploadUrlRequest request
    ) {

        return ApiResponse.<PresignedUploadResponse>ok()
                .data(fileService.generateUploadUrl(request))
                .build(
        );
    }
}
