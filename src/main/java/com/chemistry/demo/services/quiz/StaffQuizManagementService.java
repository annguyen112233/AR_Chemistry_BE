package com.chemistry.demo.services.quiz;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.quiz.StaffLessonQuizOverviewResponse;
import com.chemistry.demo.dto.response.quiz.StaffQuizDetailResponse;
import com.chemistry.demo.dto.response.quiz.StaffQuizSummaryResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface StaffQuizManagementService {
    PageResponse<StaffLessonQuizOverviewResponse> getLessonQuizOverview(Pageable pageable);
    PageResponse<StaffQuizSummaryResponse> getQuizzesByLesson(String lessonCode, Pageable pageable);
    StaffQuizDetailResponse getQuizDetail(String quizCode, Pageable pageable);
    StaffQuizSummaryResponse publishQuiz(String quizCode);
}
