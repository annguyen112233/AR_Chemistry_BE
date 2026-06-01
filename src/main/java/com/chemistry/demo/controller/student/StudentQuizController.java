package com.chemistry.demo.controller.student;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.request.quiz.student.SubmitQuizRequest;
import com.chemistry.demo.dto.response.quiz.student.StudentPublishedQuizResponse;
import com.chemistry.demo.dto.response.quiz.student.StudentQuizDetailResponse;
import com.chemistry.demo.dto.response.quiz.student.StudentQuizSummaryResponse;
import com.chemistry.demo.dto.response.quiz.student.SubmitQuizResponse;
import com.chemistry.demo.services.quiz.StudentQuizService;
import lombok.RequiredArgsConstructor;
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
}
