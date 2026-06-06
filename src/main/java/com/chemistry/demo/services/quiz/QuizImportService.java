package com.chemistry.demo.services.quiz;

import com.chemistry.demo.dto.request.quiz.StartQuizImportRequest;
import com.chemistry.demo.dto.response.quiz.staff.StartQuizImportResponse;

public interface QuizImportService {
    StartQuizImportResponse startImport(StartQuizImportRequest request);
}
