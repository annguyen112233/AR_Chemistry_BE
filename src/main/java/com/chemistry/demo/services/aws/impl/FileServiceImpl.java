package com.chemistry.demo.services.aws.impl;

import com.chemistry.demo.config.properties.AwsProperties;
import com.chemistry.demo.dto.request.upload.GenerateUploadUrlRequest;
import com.chemistry.demo.dto.response.upload.PresignedUploadResponse;
import com.chemistry.demo.entity.UploadFile;
import com.chemistry.demo.entity.UploadPurpose;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.UploadStatus;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.UploadErrorCode;
import com.chemistry.demo.repository.UploadFileRepository;
import com.chemistry.demo.repository.UploadPurposeRepository;
import com.chemistry.demo.services.aws.FileService;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
        private final S3Presigner s3Presigner;

        private final AwsProperties awsProperties;
        private final UploadPurposeRepository uploadPurposeRepository;
        private final UploadFileRepository uploadFileRepository;
        private final SecurityUtils securityUtils;

        @Override
        @PreAuthorize("hasAuthority('UPLOAD_FILE')")
        public PresignedUploadResponse generateUploadUrl(
                        GenerateUploadUrlRequest request) {
                User currentUser = securityUtils.getCurrentUserCognitoSub();

                UploadPurpose purpose = uploadPurposeRepository.findByCodeAndActiveTrue(request.getPurposeCode())
                                .orElseThrow(() -> new AppException(UploadErrorCode.UPLOAD_PURPOSE_NOT_FOUND));

                if (request.getFileSize() > purpose.getMaxFileSize()) {
                        throw new AppException(UploadErrorCode.FILE_SIZE_EXCEEDED);
                }

                boolean isAllowedContentType = Arrays.stream(purpose.getAllowedContentTypes().split(","))
                                .anyMatch(allowedType -> allowedType.trim().equalsIgnoreCase(request.getContentType()));

                if (!isAllowedContentType) {
                        throw new AppException(UploadErrorCode.INVALID_CONTENT_TYPE);
                }

                String safeFileName = sanitizeFileName(request.getFileName());
                if (safeFileName.isBlank()) {
                        throw new AppException(UploadErrorCode.INVALID_FILE_NAME);
                }

                LocalDateTime now = LocalDateTime.now();
                String datePath = String.format("%04d/%02d/%02d", now.getYear(), now.getMonthValue(),
                                now.getDayOfMonth());
                String key = purpose.getFolderPrefix() + "/" + datePath + "/" + UUID.randomUUID() + "-" + safeFileName;

                UploadFile uploadFile = UploadFile.builder()
                                .originalFileName(request.getFileName())
                                .safeFileName(safeFileName)
                                .contentType(request.getContentType())
                                .fileSize(request.getFileSize())
                                .storageKey(key)
                                .purposeCode(purpose.getCode())
                                .uploadStatus(UploadStatus.PENDING)
                                .uploadedBy(currentUser.getCognitoSub())
                                .build();

                uploadFile = uploadFileRepository.save(uploadFile);

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                .bucket(awsProperties.getS3().getBucketName())
                                .key(key)
                                .contentType(request.getContentType())
                                .contentLength(request.getFileSize()) // AWS sẽ block nếu file thực tế không khớp size
                                                                      // này
                                .build();

                PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                                .signatureDuration(Duration.ofMinutes(5))
                                .putObjectRequest(putObjectRequest)
                                .build();

                PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

                String fileUrl = "https://" +
                                awsProperties.getS3().getBucketName() +
                                ".s3." +
                                awsProperties.getRegion() +
                                ".amazonaws.com/" +
                                key;

                uploadFile.setFileUrl(fileUrl);
                uploadFileRepository.save(uploadFile);

                return PresignedUploadResponse.builder()
                                .uploadFileId(uploadFile.getId())
                                .uploadUrl(presignedRequest.url().toString())
                                .fileUrl(fileUrl)
                                .storageKey(key)
                                .contentType(request.getContentType())
                                .build();
        }

        private String sanitizeFileName(String fileName) {
                if (fileName == null)
                        return "";
                String safeName = fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
                safeName = safeName.replaceAll("_+", "_");
                return safeName;
        }
}
