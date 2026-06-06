package com.chemistry.demo.services.quiz;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.quiz.student.SubmitQuizRequest;
import com.chemistry.demo.dto.response.quiz.student.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentQuizService {

    StudentQuizSummaryResponse getPublishedQuizByLesson(String lessonCode);

    StudentQuizDetailResponse getQuizQuestions(String quizCode);

    SubmitQuizResponse submitQuiz(String quizCode, SubmitQuizRequest request);
    List<StudentPublishedQuizResponse> getPublishedQuizzes();
    PageResponse<StudentQuizAttemptHistoryResponse> getMyQuizAttemptHistory(
            String quizCode,
            Pageable pageable
    );
    StudentQuizAttemptDetailResponse getMyQuizAttemptDetail(String attemptCode);
}
