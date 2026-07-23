package com.chemistry.demo.controller.staff;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffQuizAttemptDetailResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffQuizAttemptResponse;
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
    public ApiResponse<PageResponse<StaffQuizAttemptResponse>> getQuizAttempts(
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<StaffQuizAttemptResponse>>ok()
                .data(staffQuizAttemptService.getQuizAttempts(pageable))
                .build();
    }

    @GetMapping("/{attemptCode}")
    public ApiResponse<StaffQuizAttemptDetailResponse> getQuizAttemptDetail(
            @PathVariable String attemptCode
    ) {
        return ApiResponse.<StaffQuizAttemptDetailResponse>ok()
                .data(staffQuizAttemptService.getQuizAttemptDetail(attemptCode))
                .build();
    }
}
