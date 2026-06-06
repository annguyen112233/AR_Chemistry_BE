package com.chemistry.demo.services.quiz;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffLessonQuizOverviewResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffQuizDetailResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffQuizSummaryResponse;
import org.springframework.data.domain.Pageable;


public interface StaffQuizManagementService {
    PageResponse<StaffLessonQuizOverviewResponse> getLessonQuizOverview(Pageable pageable);
    PageResponse<StaffQuizSummaryResponse> getQuizzesByLesson(String lessonCode, Pageable pageable);
    StaffQuizDetailResponse getQuizDetail(String quizCode, Pageable pageable);
    StaffQuizSummaryResponse publishQuiz(String quizCode);

}
