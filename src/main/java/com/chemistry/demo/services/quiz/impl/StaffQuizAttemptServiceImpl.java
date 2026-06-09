package com.chemistry.demo.services.quiz.impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.entity.*;
import com.chemistry.demo.repository.QuizAttemptAnswerRepository;
import com.chemistry.demo.repository.QuizAttemptRepository;
import com.chemistry.demo.services.quiz.StaffQuizAttemptService;
import com.chemistry.demo.utils.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffQuizAttemptServiceImpl implements StaffQuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAttemptAnswerRepository quizAttemptAnswerRepository;

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")
    public PageResponse<com.chemistry.demo.dto.quizCSV.staff.StaffQuizAttemptResponse> getQuizAttempts(Pageable pageable) {
        Page<QuizAttempt> attempts =
                quizAttemptRepository.findAllByOrderByCreatedAtDesc(pageable);

        return PageResponseUtils.toPageResponse(
                attempts,
                this::toAttemptResponse
        );
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")
    public com.chemistry.demo.dto.quizCSV.staff.StaffQuizAttemptDetailResponse getQuizAttemptDetail(String attemptCode) {
        QuizAttempt attempt = quizAttemptRepository.findByAttemptCode(attemptCode)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found: " + attemptCode));

        List<QuizAttemptAnswer> answers =
                quizAttemptAnswerRepository.findByAttemptOrderByQuestionOrderAsc(attempt);

        Quiz quiz = attempt.getQuiz();
        Lesson lesson = quiz.getLesson();
        User student = attempt.getStudent();

        List<com.chemistry.demo.dto.quizCSV.staff.StaffQuizAttemptDetailResponse.AnswerDetail> answerDetails =
                answers.stream()
                        .map(answer -> {
                            QuizQuestion question = answer.getQuestion();

                            return com.chemistry.demo.dto.quizCSV.staff.StaffQuizAttemptDetailResponse.AnswerDetail.builder()
                                    .questionId(question.getId())
                                    .questionOrder(question.getQuestionOrder())
                                    .questionText(question.getQuestionText())
                                    .studentAnswer(answer.getStudentAnswer())
                                    .correctAnswer(answer.getCorrectAnswer())
                                    .correct(answer.getCorrect())
                                    .explanation(answer.getExplanation())
                                    .build();
                        })
                        .toList();

        return com.chemistry.demo.dto.quizCSV.staff.StaffQuizAttemptDetailResponse.builder()
                .attemptCode(attempt.getAttemptCode())
                .studentId(student.getCognitoSub())
                .studentName(getStudentName(student))
                .studentEmail(student.getEmail())
                .quizCode(quiz.getQuizCode())
                .quizTitle(quiz.getTitle())
                .lessonCode(lesson.getLessonCode())
                .lessonTitle(lesson.getTitle())
                .score(attempt.getScore())
                .totalQuestions(attempt.getTotalQuestions())
                .correctCount(attempt.getCorrectCount())
                .status(attempt.getStatus())
                .submittedAt(attempt.getCreatedAt())
                .answers(answerDetails)
                .build();
    }

    private com.chemistry.demo.dto.quizCSV.staff.StaffQuizAttemptResponse toAttemptResponse(QuizAttempt attempt) {
        Quiz quiz = attempt.getQuiz();
        Lesson lesson = quiz.getLesson();
        User student = attempt.getStudent();

        return com.chemistry.demo.dto.quizCSV.staff.StaffQuizAttemptResponse.builder()
                .attemptCode(attempt.getAttemptCode())
                .studentId(student.getCognitoSub())
                .studentName(getStudentName(student))
                .studentEmail(student.getEmail())
                .quizCode(quiz.getQuizCode())
                .quizTitle(quiz.getTitle())
                .lessonCode(lesson.getLessonCode())
                .lessonTitle(lesson.getTitle())
                .score(attempt.getScore())
                .totalQuestions(attempt.getTotalQuestions())
                .correctCount(attempt.getCorrectCount())
                .status(attempt.getStatus())
                .submittedAt(attempt.getCreatedAt())
                .build();
    }

    private String getStudentName(User student) {
        if (student.getFullName() != null && !student.getFullName().isBlank()) {
            return student.getFullName();
        }

        if (student.getEmail() != null && !student.getEmail().isBlank()) {
            return student.getEmail();
        }

        return "Unknown student";
    }
}
