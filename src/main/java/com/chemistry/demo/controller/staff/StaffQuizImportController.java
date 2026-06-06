package com.chemistry.demo.controller.staff;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.request.quiz.QuizImportUploadUrlRequest;
import com.chemistry.demo.dto.request.quiz.StartQuizImportRequest;
import com.chemistry.demo.dto.response.quiz.staff.StartQuizImportResponse;
import com.chemistry.demo.services.quiz.QuizImportService;
import com.chemistry.demo.services.quiz.QuizImportUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staff/quiz-import")
@RequiredArgsConstructor
public class StaffQuizImportController {
    private final QuizImportUploadService quizImportUploadService;
    private final QuizImportService quizImportService;

    @PostMapping("/upload-url")
    public ApiResponse<?> createUploadUrl(@RequestBody QuizImportUploadUrlRequest request) {
        return ApiResponse.ok()
                .data(quizImportUploadService.createUploadUrl(request))
                .build();
    }

    @PostMapping("/start")
    public ApiResponse<StartQuizImportResponse> startImport(@RequestBody StartQuizImportRequest request) {
        return ApiResponse.<StartQuizImportResponse>ok()
                .data(quizImportService.startImport(request))
                .build();

    }
}
