package com.chemistry.demo.services.upload;

import com.chemistry.demo.dto.request.upload.GenerateUploadUrlRequest;
import com.chemistry.demo.dto.response.upload.PresignedUploadResponse;

public interface UploadService {
    PresignedUploadResponse generateUploadUrl(GenerateUploadUrlRequest request);
}
