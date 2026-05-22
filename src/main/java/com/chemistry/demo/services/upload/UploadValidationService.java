package com.chemistry.demo.services.upload;

import com.chemistry.demo.dto.request.upload.GenerateUploadUrlRequest;
import com.chemistry.demo.enums.UploadType;
import org.springframework.stereotype.Service;

import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.UploadErrorCode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class UploadValidationService {

    private static final List<String> DANGEROUS_EXTENSIONS = Arrays.asList(
            ".exe", ".bat", ".cmd", ".sh", ".cgi", ".pl", ".php", ".jsp", ".asp", ".js");

    private static final Map<UploadType, List<String>> ALLOWED_CONTENT_TYPES = Map.of(
            UploadType.FEEDBACK, Arrays.asList("image/jpeg", "image/png", "application/pdf", "text/plain"),
            UploadType.AVATAR, Arrays.asList("image/jpeg", "image/png", "image/webp"),
            UploadType.RESEARCH_PAPER,
            Arrays.asList("application/pdf", "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
            UploadType.EVIDENCE, Arrays.asList("image/jpeg", "image/png", "application/pdf"),
            UploadType.REPORT,
            Arrays.asList("application/pdf", "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
            UploadType.DATASET,
            Arrays.asList("text/csv", "application/json", "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
            UploadType.THUMBNAIL, Arrays.asList("image/jpeg", "image/png", "image/webp"),
            UploadType.PUBLICATION_FILE, Arrays.asList("application/pdf"));

    public void validateRequest(GenerateUploadUrlRequest request) {
        validateUploadType(request.getUploadType());
        validateFileName(request.getFileName());
        validateContentType(request.getUploadType(), request.getContentType());
    }

    private void validateUploadType(UploadType uploadType) {
        if (uploadType == null) {
            throw new AppException(UploadErrorCode.UPLOAD_TYPE_REQUIRED);
        }
    }

    private void validateFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new AppException(UploadErrorCode.FILE_NAME_REQUIRED);
        }

        String lowerCaseName = fileName.toLowerCase();
        for (String ext : DANGEROUS_EXTENSIONS) {
            if (lowerCaseName.endsWith(ext)) {
                throw new AppException(UploadErrorCode.DANGEROUS_FILE_TYPE);
            }
        }
    }

    private void validateContentType(UploadType uploadType, String contentType) {
        if (contentType == null || contentType.trim().isEmpty()) {
            throw new AppException(UploadErrorCode.CONTENT_TYPE_REQUIRED);
        }

        List<String> allowedForType = ALLOWED_CONTENT_TYPES.get(uploadType);
        if (allowedForType != null && !allowedForType.contains(contentType.toLowerCase())) {
            throw new AppException(UploadErrorCode.INVALID_CONTENT_TYPE);
        }
    }
}
