package com.chemistry.demo.services.quiz.impl;

import com.chemistry.demo.dto.request.quiz.QuizImportUploadUrlRequest;
import com.chemistry.demo.dto.response.quiz.QuizImportUploadUrlResponse;
import com.chemistry.demo.repository.LessonRepository;
import com.chemistry.demo.services.aws.S3Service;
import com.chemistry.demo.services.quiz.QuizImportUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizImportUploadServiceImpl implements QuizImportUploadService {

    private final S3Service s3Service;
    private final LessonRepository lessonRepository;
    @Override
    public QuizImportUploadUrlResponse createUploadUrl(QuizImportUploadUrlRequest request) {
        if (request.getLessonCode() == null || request.getLessonCode().isBlank()) {
            throw new IllegalArgumentException("lessonCode is required");
        }

        if (!lessonRepository.existsByLessonCode(request.getLessonCode())) {
            throw new RuntimeException("Lesson not found: " + request.getLessonCode());
        }

        String contentType = request.getContentType();
        if (!"text/csv".equals(contentType) && !"application/vnd.ms-excel".equals(contentType)) {
            throw new IllegalArgumentException("Only CSV file is allowed");
        }

        long maxSize = 10 * 1024 * 1024L;
        if (request.getFileSize() == null || request.getFileSize() <= 0 || request.getFileSize() > maxSize) {
            throw new IllegalArgumentException("Invalid file size");
        }

        String safeLessonCode = request.getLessonCode().replaceAll("[^a-zA-Z0-9_-]", "_");

        String key = "quiz-import/"
                + LocalDate.now()
                + "/"
                + safeLessonCode
                + "/"
                + UUID.randomUUID()
                + ".csv";

        String uploadUrl = s3Service.generatePresignedPutUrl(
                key,
                contentType,
                request.getFileSize()
        );

        String fileUrl = s3Service.buildFileUrl(key);

        return new QuizImportUploadUrlResponse(key, uploadUrl, fileUrl);
    }
}
