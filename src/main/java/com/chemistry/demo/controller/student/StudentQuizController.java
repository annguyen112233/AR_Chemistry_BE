package com.chemistry.demo.controller.student;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.quiz.student.SubmitQuizRequest;
import com.chemistry.demo.dto.response.quiz.student.*;
import com.chemistry.demo.services.quiz.StudentQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentQuizController {
    private final StudentQuizService studentQuizService;

    @GetMapping("/lessons/{lessonCode}/quiz")
    public ApiResponse<StudentQuizSummaryResponse> getPublishedQuizByLesson(
            @PathVariable String lessonCode
    ) {
        return ApiResponse.<StudentQuizSummaryResponse>ok()
                .data(studentQuizService.getPublishedQuizByLesson(lessonCode))
                .build();
    }

    @GetMapping("/quizzes/{quizCode}/questions")
    public ApiResponse<StudentQuizDetailResponse> getQuizQuestions(
            @PathVariable String quizCode
    ) {
        return ApiResponse.<StudentQuizDetailResponse>ok()
                .data(studentQuizService.getQuizQuestions(quizCode))
                .build();
    }

    @PostMapping("/quizzes/{quizCode}/submit")
    public ApiResponse<SubmitQuizResponse> submitQuiz(
            @PathVariable String quizCode,
            @RequestBody SubmitQuizRequest request
    ) {
        return ApiResponse.<SubmitQuizResponse>ok()
                .data(studentQuizService.submitQuiz(quizCode, request))
                .build();
    }

    @GetMapping("/quizzes/published")
    public ApiResponse<List<StudentPublishedQuizResponse>> getPublishedQuizzes() {
        return ApiResponse.<List<StudentPublishedQuizResponse>>ok()
                .data(studentQuizService.getPublishedQuizzes())
                .build();
    }

    @GetMapping("/quiz-attempts")
    public ApiResponse<PageResponse<StudentQuizAttemptHistoryResponse>> getMyQuizAttemptHistory(
            @RequestParam(required = false) String quizCode,
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<StudentQuizAttemptHistoryResponse>>ok()
                .data(studentQuizService.getMyQuizAttemptHistory(quizCode, pageable))
                .build();
    }

    @GetMapping("/quiz-attempts/{attemptCode}")
    public ApiResponse<StudentQuizAttemptDetailResponse> getMyQuizAttemptDetail(
            @PathVariable String attemptCode
    ) {
        return ApiResponse.<StudentQuizAttemptDetailResponse>ok()
                .data(studentQuizService.getMyQuizAttemptDetail(attemptCode))
                .build();
    }
}
