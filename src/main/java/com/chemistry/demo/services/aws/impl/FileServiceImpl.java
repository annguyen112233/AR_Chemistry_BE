package com.chemistry.demo.services.aws.impl;

import com.chemistry.demo.config.properties.AwsProperties;
import com.chemistry.demo.dto.request.upload.GenerateUploadUrlRequest;
import com.chemistry.demo.dto.response.upload.PresignedUploadResponse;
import com.chemistry.demo.services.aws.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final S3Presigner s3Presigner;

    private final AwsProperties awsProperties;
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_TEACHER')")
    public PresignedUploadResponse generateUploadUrl(
            GenerateUploadUrlRequest request
    ) {

        String key =
                "feedback/" +
                        UUID.randomUUID() +
                        "-" +
                        request.getFileName();

        PutObjectRequest putObjectRequest =
                PutObjectRequest.builder()
                        .bucket(awsProperties.getS3().getBucketName())
                        .key(key)
                        .contentType(request.getContentType())
                        .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(5))
                        .putObjectRequest(putObjectRequest)
                        .build();

        PresignedPutObjectRequest presignedRequest =
                s3Presigner.presignPutObject(presignRequest);

        String fileUrl =
                "https://" +
                        awsProperties.getS3().getBucketName() +
                        ".s3." +
                        awsProperties.getRegion() +
                        ".amazonaws.com/" +
                        key;

        return PresignedUploadResponse.builder()
                .uploadUrl(presignedRequest.url().toString())
                .fileUrl(fileUrl)
                .build();
    }
}
