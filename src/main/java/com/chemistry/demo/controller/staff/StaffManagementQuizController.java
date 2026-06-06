package com.chemistry.demo.controller.staff;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffLessonQuizOverviewResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffQuizDetailResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffQuizSummaryResponse;
import com.chemistry.demo.services.lesson.LessonService;
import com.chemistry.demo.services.quiz.StaffQuizManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff/quiz-management")
@RequiredArgsConstructor
public class StaffManagementQuizController {
    private final StaffQuizManagementService staffQuizManagementService;
    private final LessonService lessonService;

    @GetMapping("/lessons")
    public ApiResponse<PageResponse<StaffLessonQuizOverviewResponse>> getQuizOverview(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ApiResponse.<PageResponse<StaffLessonQuizOverviewResponse>>ok()
                .data(staffQuizManagementService.getLessonQuizOverview(pageable))
                .build();
    }

    @GetMapping("/lessons/{lessonCode}/quizzes")
    public ApiResponse<PageResponse<StaffQuizSummaryResponse>> getQuizzesByLesson
            (@PathVariable String lessonCode,
             @PageableDefault(size = 10) Pageable pageable) {

        return ApiResponse.<PageResponse<StaffQuizSummaryResponse>>ok()
                .data(staffQuizManagementService.getQuizzesByLesson(lessonCode, pageable))
                .build();
    }

    @PostMapping("/quizzes/{quizCode}/publish")
    public ApiResponse<StaffQuizSummaryResponse> publishQuiz(
            @PathVariable String quizCode
    ) {
        return ApiResponse.<StaffQuizSummaryResponse>ok()
                .data(staffQuizManagementService.publishQuiz(quizCode))
                .build();
    }

    @GetMapping("/quizzes/{quizCode}/questions")
    public ApiResponse<StaffQuizDetailResponse> getQuizDetail(
            @PathVariable String quizCode,
            @PageableDefault(size = 10) Pageable pageable) {

        return ApiResponse.<StaffQuizDetailResponse>ok()
                .data(staffQuizManagementService.getQuizDetail(quizCode, pageable))
                .build();
    }

    @GetMapping("/content/{lessonCode}")
    public ApiResponse<?> getLesson(@PathVariable String lessonCode) {
        return ApiResponse.ok()
                .data(lessonService.getLessonDetails(lessonCode))
                .build();
    }
}
