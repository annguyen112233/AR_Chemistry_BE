package com.chemistry.demo.services.aws;

import com.chemistry.demo.dto.request.upload.GenerateUploadUrlRequest;
import com.chemistry.demo.dto.response.upload.PresignedUploadResponse;

public interface FileService {

    PresignedUploadResponse generateUploadUrl(GenerateUploadUrlRequest request);
}
