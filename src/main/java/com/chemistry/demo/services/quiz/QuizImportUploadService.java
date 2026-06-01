package com.chemistry.demo.services.quiz;

import com.chemistry.demo.dto.request.quiz.QuizImportUploadUrlRequest;
import com.chemistry.demo.dto.response.quiz.QuizImportUploadUrlResponse;

public interface QuizImportUploadService {
    QuizImportUploadUrlResponse createUploadUrl(QuizImportUploadUrlRequest request);
}
