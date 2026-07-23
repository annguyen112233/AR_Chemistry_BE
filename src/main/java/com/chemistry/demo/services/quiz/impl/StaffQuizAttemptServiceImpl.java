package com.chemistry.demo.services.quiz.impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffQuizAttemptDetailResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffQuizAttemptResponse;
import com.chemistry.demo.entity.Quiz;
import com.chemistry.demo.entity.QuizAttempt;
import com.chemistry.demo.entity.QuizAttemptAnswer;
import com.chemistry.demo.entity.QuizQuestion;
import com.chemistry.demo.entity.ReactionDefinition;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.QuizErrorCode;
import com.chemistry.demo.repository.QuizAttemptAnswerRepository;
import com.chemistry.demo.repository.QuizAttemptRepository;
import com.chemistry.demo.services.quiz.StaffQuizAttemptService;
import com.chemistry.demo.utils.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffQuizAttemptServiceImpl
        implements StaffQuizAttemptService {

    private final QuizAttemptRepository
            quizAttemptRepository;

    private final QuizAttemptAnswerRepository
            quizAttemptAnswerRepository;

    @Override
    @PreAuthorize(
            "hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')"
    )
    @Transactional(readOnly = true)
    public PageResponse<StaffQuizAttemptResponse>
    getQuizAttempts(
            Pageable pageable
    ) {
        Page<QuizAttempt> attempts =
                quizAttemptRepository
                        .findAllByOrderByCreatedAtDesc(
                                pageable
                        );

        return PageResponseUtils.toPageResponse(
                attempts,
                this::toAttemptResponse
        );
    }

    @Override
    @PreAuthorize(
            "hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')"
    )
    @Transactional(readOnly = true)
    public StaffQuizAttemptDetailResponse
    getQuizAttemptDetail(
            String attemptCode
    ) {
        QuizAttempt attempt =
                quizAttemptRepository
                        .findByAttemptCode(
                                normalizeAttemptCode(
                                        attemptCode
                                )
                        )
                        .orElseThrow(() ->
                                new AppException(
                                        QuizErrorCode.QUIZ_ATTEMPT_NOT_FOUND
                                )
                        );

        List<QuizAttemptAnswer> answers =
                quizAttemptAnswerRepository
                        .findByAttemptOrderByQuestionOrderAsc(
                                attempt
                        );

        Quiz quiz =
                attempt.getQuiz();

        ReactionDefinition reaction =
                quiz.getReaction();

        User student =
                attempt.getStudent();

        List<StaffQuizAttemptDetailResponse.AnswerDetail>
                answerDetails =
                answers.stream()
                        .map(answer ->
                                toAnswerDetail(answer)
                        )
                        .toList();

        return StaffQuizAttemptDetailResponse.builder()
                .attemptCode(
                        attempt.getAttemptCode()
                )
                .studentId(
                        student.getCognitoSub()
                )
                .studentName(
                        getStudentName(student)
                )
                .studentEmail(
                        student.getEmail()
                )
                .quizCode(
                        quiz.getQuizCode()
                )
                .quizTitle(
                        quiz.getTitle()
                )
                .reactionId(
                        reaction.getId()
                )
                .reactionCode(
                        reaction.getCode()
                )
                .reactionName(
                        reaction.getName()
                )
                .equation(
                        reaction.getEquation()
                )
                .grade(
                        reaction.getGrade()
                )
                .reactionCategory(
                        reaction
                                .getReactionCategory()
                                .name()
                )
                .score(
                        attempt.getScore()
                )
                .totalQuestions(
                        attempt.getTotalQuestions()
                )
                .correctCount(
                        attempt.getCorrectCount()
                )
                .status(
                        attempt.getStatus()
                )
                .startedAt(
                        attempt.getStartedAt()
                )
                .expiredAt(
                        attempt.getExpiredAt()
                )
                .submittedAt(
                        attempt.getSubmittedAt()
                )
                .answers(
                        answerDetails
                )
                .build();
    }

    private StaffQuizAttemptResponse
    toAttemptResponse(
            QuizAttempt attempt
    ) {
        Quiz quiz =
                attempt.getQuiz();

        ReactionDefinition reaction =
                quiz.getReaction();

        User student =
                attempt.getStudent();

        return StaffQuizAttemptResponse.builder()
                .attemptCode(
                        attempt.getAttemptCode()
                )
                .studentId(
                        student.getCognitoSub()
                )
                .studentName(
                        getStudentName(student)
                )
                .studentEmail(
                        student.getEmail()
                )
                .quizCode(
                        quiz.getQuizCode()
                )
                .quizTitle(
                        quiz.getTitle()
                )
                .reactionId(
                        reaction.getId()
                )
                .reactionCode(
                        reaction.getCode()
                )
                .reactionName(
                        reaction.getName()
                )
                .equation(
                        reaction.getEquation()
                )
                .grade(
                        reaction.getGrade()
                )
                .reactionCategory(
                        reaction
                                .getReactionCategory()
                                .name()
                )
                .score(
                        Double.valueOf(attempt.getScore())
                )
                .totalQuestions(
                        attempt.getTotalQuestions()
                )
                .correctCount(
                        attempt.getCorrectCount()
                )
                .status(
                        attempt.getStatus()
                )
                .startedAt(
                        attempt.getStartedAt()
                )
                .expiredAt(
                        attempt.getExpiredAt()
                )
                .submittedAt(
                        attempt.getSubmittedAt()
                )
                .build();
    }

    private StaffQuizAttemptDetailResponse.AnswerDetail
    toAnswerDetail(
            QuizAttemptAnswer answer
    ) {
        QuizQuestion question =
                answer.getQuestion();

        return StaffQuizAttemptDetailResponse
                .AnswerDetail
                .builder()
                .questionId(
                        question.getId()
                )
                .questionOrder(
                        question.getQuestionOrder()
                )
                .questionText(
                        question.getQuestionText()
                )
                .studentAnswer(
                        answer.getStudentAnswer()
                )
                .correctAnswer(
                        answer.getCorrectAnswer()
                )
                .correct(
                        answer.getCorrect()
                )
                .explanation(
                        answer.getExplanation()
                )
                .answeredAt(
                        answer.getAnsweredAt()
                )
                .build();
    }

    private String getStudentName(
            User student
    ) {
        if (student.getFullName() != null
                && !student.getFullName().isBlank()) {
            return student.getFullName();
        }

        if (student.getEmail() != null
                && !student.getEmail().isBlank()) {
            return student.getEmail();
        }

        return "Unknown student";
    }

    private String normalizeAttemptCode(
            String attemptCode
    ) {
        if (attemptCode == null
                || attemptCode.isBlank()) {
            throw new AppException(
                    QuizErrorCode.QUIZ_ATTEMPT_NOT_FOUND
            );
        }

        return attemptCode.trim();
    }
}