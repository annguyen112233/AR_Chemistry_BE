package com.chemistry.demo.services.quiz.impl;

import com.chemistry.demo.dto.request.quiz.QuizImportUploadUrlRequest;
import com.chemistry.demo.dto.response.quiz.staff.QuizImportUploadUrlResponse;
import com.chemistry.demo.repository.ReactionDefinitionRepository;
import com.chemistry.demo.services.aws.S3Service;
import com.chemistry.demo.services.quiz.QuizImportUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizImportUploadServiceImpl
        implements QuizImportUploadService {

    private static final long MAX_CSV_SIZE =
            2 * 1024 * 1024L;

    private final S3Service s3Service;

    private final ReactionDefinitionRepository
            reactionDefinitionRepository;

    @Override
    @PreAuthorize(
            "hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')"
    )
    public QuizImportUploadUrlResponse createUploadUrl(
            QuizImportUploadUrlRequest request
    ) {
        if (request.getReactionCode() == null
                || request.getReactionCode().isBlank()) {
            throw new IllegalArgumentException(
                    "reactionCode is required"
            );
        }

        String reactionCode =
                request.getReactionCode()
                        .trim()
                        .toUpperCase();

        if (!reactionDefinitionRepository
                .existsByCode(reactionCode)) {
            throw new RuntimeException(
                    "Reaction not found: "
                            + reactionCode
            );
        }

        String contentType =
                request.getContentType();

        boolean validContentType =
                "text/csv".equalsIgnoreCase(contentType)
                        || "application/csv"
                        .equalsIgnoreCase(contentType)
                        || "application/vnd.ms-excel"
                        .equalsIgnoreCase(contentType);

        if (!validContentType) {
            throw new IllegalArgumentException(
                    "Only CSV file is allowed"
            );
        }

        if (request.getFileSize() == null
                || request.getFileSize() <= 0
                || request.getFileSize() > MAX_CSV_SIZE) {
            throw new IllegalArgumentException(
                    "CSV file size must be between 1 byte and 2 MB"
            );
        }

        String safeReactionCode =
                reactionCode.replaceAll(
                        "[^a-zA-Z0-9_-]",
                        "_"
                );

        String key =
                "quiz-imports/"
                        + LocalDate.now()
                        + "/"
                        + safeReactionCode
                        + "/"
                        + UUID.randomUUID()
                        + ".csv";

        String uploadUrl =
                s3Service.generatePresignedPutUrl(
                        key,
                        contentType,
                        request.getFileSize()
                );

        String fileUrl =
                s3Service.buildFileUrl(key);

        log.info(
                "Created quiz CSV upload URL for reaction {}, key={}",
                reactionCode,
                key
        );

        return new QuizImportUploadUrlResponse(
                key,
                uploadUrl,
                fileUrl
        );
    }
}