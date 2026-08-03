package com.chemistry.demo.controller.staff;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.services.quiz.StaffQuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staff/quiz-attempts")
@RequiredArgsConstructor
public class StaffQuizAttemptController {

    private final StaffQuizAttemptService staffQuizAttemptService;

    @GetMapping
    public ApiResponse<PageResponse<com.chemistry.demo.dto.quizCSV.staff.StaffQuizAttemptResponse>> getQuizAttempts(
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<com.chemistry.demo.dto.quizCSV.staff.StaffQuizAttemptResponse>>ok()
                .data(staffQuizAttemptService.getQuizAttempts(pageable))
                .build();
    }

    @GetMapping("/{attemptCode}")
    public ApiResponse<com.chemistry.demo.dto.quizCSV.staff.StaffQuizAttemptDetailResponse> getQuizAttemptDetail(
            @PathVariable String attemptCode
    ) {
        return ApiResponse.<com.chemistry.demo.dto.quizCSV.staff.StaffQuizAttemptDetailResponse>ok()
                .data(staffQuizAttemptService.getQuizAttemptDetail(attemptCode))
                .build();
    }
}
