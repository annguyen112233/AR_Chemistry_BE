package com.chemistry.demo.services.quiz;

import com.chemistry.demo.dto.request.quiz.student.SubmitQuizRequest;
import com.chemistry.demo.dto.response.quiz.student.StudentPublishedQuizResponse;
import com.chemistry.demo.dto.response.quiz.student.StudentQuizDetailResponse;
import com.chemistry.demo.dto.response.quiz.student.StudentQuizSummaryResponse;
import com.chemistry.demo.dto.response.quiz.student.SubmitQuizResponse;

import java.util.List;

public interface StudentQuizService {

    StudentQuizSummaryResponse getPublishedQuizByLesson(String lessonCode);

    StudentQuizDetailResponse getQuizQuestions(String quizCode);

    SubmitQuizResponse submitQuiz(String quizCode, SubmitQuizRequest request);
    List<StudentPublishedQuizResponse> getPublishedQuizzes();
}
