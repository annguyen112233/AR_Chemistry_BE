package com.chemistry.demo.services.quiz;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.quiz.student.CompleteArRequest;
import com.chemistry.demo.dto.request.quiz.student.SaveQuizAnswerRequest;
import com.chemistry.demo.dto.response.quiz.student.*;
import com.chemistry.demo.enums.ReactionCategory;
import org.springframework.data.domain.Pageable;

public interface StudentQuizService {

    PageResponse<StudentReactionResponse> getReactions(
            Integer grade,
            ReactionCategory reactionCategory,
            String keyword,
            Pageable pageable
    );

    StudentReactionDetailResponse getReactionDetail(
            String reactionId
    );

    StudentQuizSummaryResponse getPublishedQuizByReaction(
            String reactionId
    );

    StartQuizResponse startQuiz(
            String reactionId
    );

    CompleteArResponse completeAr(
            String attemptCode,
            CompleteArRequest request
    );

    QuizAttemptStateResponse getAttemptState(
            String attemptCode
    );

    StudentQuizContentResponse getQuizContent(
            String attemptCode
    );

    void saveAnswer(
            String attemptCode,
            String questionId,
            SaveQuizAnswerRequest request
    );

    SubmitQuizResponse submitQuiz(
            String attemptCode
    );

    StudentQuizAttemptDetailResponse getMyQuizAttemptDetail(
            String attemptCode
    );

    PageResponse<StudentQuizAttemptHistoryResponse>
    getReactionAttemptHistory(
            String reactionId,
            Pageable pageable
    );

    void abandonAttempt(
            String attemptCode
    );
}