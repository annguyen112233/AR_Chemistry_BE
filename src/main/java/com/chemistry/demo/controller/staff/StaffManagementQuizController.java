package com.chemistry.demo.controller.staff;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffQuizDetailResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffQuizSummaryResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffReactionQuizOverviewResponse;
import com.chemistry.demo.services.quiz.StaffQuizManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff/quiz-management")
@RequiredArgsConstructor
public class StaffManagementQuizController {

    private final StaffQuizManagementService
            staffQuizManagementService;

    /**
     * Danh sách reaction do Admin tạo.
     * Staff dùng danh sách này để xem reaction nào đã có quiz.
     */
    @GetMapping("/reactions")
    public ApiResponse<
            PageResponse<StaffReactionQuizOverviewResponse>
            > getReactionQuizOverview(
            @PageableDefault(size = 10)
            Pageable pageable
    ) {
        return ApiResponse
                .<PageResponse<
                        StaffReactionQuizOverviewResponse
                        >>ok()
                .data(
                        staffQuizManagementService
                                .getReactionQuizOverview(
                                        pageable
                                )
                )
                .build();
    }

    /**
     * Lấy tất cả phiên bản quiz của một reaction.
     */
    @GetMapping(
            "/reactions/{reactionCode}/quizzes"
    )
    public ApiResponse<
            PageResponse<StaffQuizSummaryResponse>
            > getQuizzesByReaction(
            @PathVariable String reactionCode,
            @PageableDefault(size = 10)
            Pageable pageable
    ) {
        return ApiResponse
                .<PageResponse<
                        StaffQuizSummaryResponse
                        >>ok()
                .data(
                        staffQuizManagementService
                                .getQuizzesByReaction(
                                        reactionCode,
                                        pageable
                                )
                )
                .build();
    }

    /**
     * Xem chi tiết quiz và danh sách câu hỏi.
     */
    @GetMapping("/quizzes/{quizCode}")
    public ApiResponse<StaffQuizDetailResponse>
    getQuizDetail(
            @PathVariable String quizCode,
            @PageableDefault(size = 10)
            Pageable pageable
    ) {
        return ApiResponse
                .<StaffQuizDetailResponse>ok()
                .data(
                        staffQuizManagementService
                                .getQuizDetail(
                                        quizCode,
                                        pageable
                                )
                )
                .build();
    }

    /**
     * Publish quiz READY.
     * Service sẽ archive quiz PUBLISHED cũ của cùng reaction.
     */
    @PostMapping(
            "/quizzes/{quizCode}/publish"
    )
    public ApiResponse<StaffQuizSummaryResponse>
    publishQuiz(
            @PathVariable String quizCode
    ) {
        return ApiResponse
                .<StaffQuizSummaryResponse>ok()
                .data(
                        staffQuizManagementService
                                .publishQuiz(quizCode)
                )
                .build();
    }
}