package com.chemistry.demo.services.quiz;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.quiz.staff.*;
import org.springframework.data.domain.Pageable;

public interface StaffQuizManagementService {

    PageResponse<StaffReactionQuizOverviewResponse>
    getReactionQuizOverview(
            Pageable pageable
    );

    PageResponse<StaffQuizSummaryResponse>
    getQuizzesByReaction(
            String reactionCode,
            Pageable pageable
    );

    StaffQuizDetailResponse getQuizDetail(
            String quizCode,
            Pageable pageable
    );

    StaffQuizSummaryResponse publishQuiz(
            String quizCode
    );
}