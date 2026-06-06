package com.chemistry.demo.services.quiz.impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.quiz.student.SubmitQuizRequest;
import com.chemistry.demo.dto.response.quiz.student.*;
import com.chemistry.demo.entity.*;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.LessonErrorCode;
import com.chemistry.demo.repository.*;
import com.chemistry.demo.services.quiz.StudentQuizService;
import com.chemistry.demo.utils.PageResponseUtils;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentQuizServiceImpl implements StudentQuizService {

    private static final String PUBLISHED = "published";
    private static final String ACTIVE = "active";

    private final LessonRepository lessonRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAttemptAnswerRepository quizAttemptAnswerRepository;
    private final SecurityUtils securityUtils;
    @Override
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public StudentQuizSummaryResponse getPublishedQuizByLesson(String lessonCode) {
        Lesson lesson = lessonRepository.findByLessonCode(lessonCode)
                .orElseThrow(() -> new AppException(LessonErrorCode.LESSON_NOT_FOUND));

        Quiz quiz = quizRepository.findTopByLessonAndStatusOrderByCreatedAtDesc(lesson, PUBLISHED)
                .orElseThrow(() -> new RuntimeException("Published quizCSV not found for lesson: " + lessonCode));

        long questionCount = quizQuestionRepository.countByQuiz(quiz);

        return StudentQuizSummaryResponse.builder()
                .quizCode(quiz.getQuizCode())
                .lessonCode(lesson.getLessonCode())
                .title(quiz.getTitle())
                .version(quiz.getVersion())
                .questionCount(questionCount)
                .build();
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public StudentQuizDetailResponse getQuizQuestions(String quizCode) {
        Quiz quiz = quizRepository.findByQuizCodeAndStatus(quizCode, PUBLISHED)
                .orElseThrow(() -> new RuntimeException("Published quizCSV not found: " + quizCode));

        List<QuizQuestion> questions =
                quizQuestionRepository.findByQuizAndStatusOrderByQuestionOrderAsc(quiz, ACTIVE);

        List<StudentQuizQuestionResponse> questionResponses = questions.stream()
                .map(q -> StudentQuizQuestionResponse.builder()
                        .id(q.getId())
                        .questionOrder(q.getQuestionOrder())
                        .type(q.getType())
                        .questionText(q.getQuestionText())
                        .optionsJson(q.getOptionsJson())
                        .difficulty(q.getDifficulty())
                        .build())
                .toList();

        return StudentQuizDetailResponse.builder()
                .quizCode(quiz.getQuizCode())
                .lessonCode(quiz.getLesson().getLessonCode())
                .title(quiz.getTitle())
                .version(quiz.getVersion())
                .questions(questionResponses)
                .build();
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @Transactional
    public SubmitQuizResponse submitQuiz(String quizCode, SubmitQuizRequest request) {
        Quiz quiz = quizRepository.findByQuizCodeAndStatus(quizCode, PUBLISHED)
                .orElseThrow(() -> new RuntimeException("Published quizCSV not found: " + quizCode));

        User student = securityUtils.getCurrentUserCognitoSub();

        List<QuizQuestion> questions =
                quizQuestionRepository.findByQuizAndStatusOrderByQuestionOrderAsc(quiz, ACTIVE);

        Map<String, String> answerMap = Optional.ofNullable(request.getAnswers())
                .orElse(List.of())
                .stream()
                .collect(Collectors.toMap(
                        SubmitQuizRequest.AnswerRequest::getQuestionId,
                        a -> normalizeAnswer(a.getAnswer()),
                        (a, b) -> a
                ));

        List<SubmitQuizResponse.QuestionResult> results = new ArrayList<>();
        List<QuizAttemptAnswer> attemptAnswers = new ArrayList<>();

        int correctCount = 0;

        QuizAttempt attempt = new QuizAttempt();
        attempt.setAttemptCode("attempt_" + System.currentTimeMillis() + "_" + UUID.randomUUID());
        attempt.setQuiz(quiz);
        attempt.setStudent(student);
        attempt.setScore(0);
        attempt.setTotalQuestions(questions.size());
        attempt.setCorrectCount(0);
        attempt.setStatus("submitted");

        quizAttemptRepository.save(attempt);

        for (QuizQuestion question : questions) {
            String questionId = question.getId();
            String studentAnswer = answerMap.getOrDefault(questionId, "");
            String correctAnswer = normalizeAnswer(question.getCorrectAnswer());

            boolean correct = studentAnswer.equals(correctAnswer);

            if (correct) {
                correctCount++;
            }

            QuizAttemptAnswer attemptAnswer = new QuizAttemptAnswer();
            attemptAnswer.setAttempt(attempt);
            attemptAnswer.setQuestion(question);
            attemptAnswer.setStudentAnswer(studentAnswer);
            attemptAnswer.setCorrectAnswer(question.getCorrectAnswer());
            attemptAnswer.setCorrect(correct);
            attemptAnswer.setExplanation(question.getExplanation());

            attemptAnswers.add(attemptAnswer);

            results.add(SubmitQuizResponse.QuestionResult.builder()
                    .questionId(questionId)
                    .correct(correct)
                    .studentAnswer(studentAnswer)
                    .correctAnswer(question.getCorrectAnswer())
                    .explanation(question.getExplanation())
                    .build());
        }

        attempt.setScore(correctCount);
        attempt.setCorrectCount(correctCount);
        quizAttemptRepository.save(attempt);

        quizAttemptAnswerRepository.saveAll(attemptAnswers);

        return SubmitQuizResponse.builder()
                .attemptCode(attempt.getAttemptCode())
                .score(correctCount)
                .total(questions.size())
                .correctCount(correctCount)
                .results(results)
                .build();
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public List<StudentPublishedQuizResponse> getPublishedQuizzes() {
        List<Quiz> quizzes = quizRepository.findByStatusOrderByCreatedAtDesc(PUBLISHED);

        return quizzes.stream()
                .map(quiz -> {
                    long questionCount = quizQuestionRepository.countByQuiz(quiz);
                    Lesson lesson = quiz.getLesson();

                    return StudentPublishedQuizResponse.builder()
                            .lessonCode(lesson.getLessonCode())
                            .lessonTitle(lesson.getTitle())
                            .chapter(lesson.getChapter())
                            .quizCode(quiz.getQuizCode())
                            .quizTitle(quiz.getTitle())
                            .version(quiz.getVersion())
                            .questionCount(questionCount)
                            .build();
                })
                .filter(item -> item.getQuestionCount() != null && item.getQuestionCount() > 0)
                .toList();
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public PageResponse<StudentQuizAttemptHistoryResponse> getMyQuizAttemptHistory(
            String quizCode,
            Pageable pageable
    ) {
        User student = securityUtils.getCurrentUserCognitoSub();

        Page<QuizAttempt> attempts;

        if (quizCode != null && !quizCode.isBlank()) {
            Quiz quiz = quizRepository.findByQuizCodeAndStatus(quizCode, PUBLISHED)
                    .orElseThrow(() -> new RuntimeException("Published quizCSV not found: " + quizCode));

            attempts = quizAttemptRepository.findByStudentAndQuizOrderByCreatedAtDesc(
                    student,
                    quiz,
                    pageable
            );
        } else {
            attempts = quizAttemptRepository.findByStudentOrderByCreatedAtDesc(
                    student,
                    pageable
            );
        }

        return PageResponseUtils.toPageResponse(
                attempts,
                this::toAttemptHistoryResponse
        );
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public StudentQuizAttemptDetailResponse getMyQuizAttemptDetail(String attemptCode) {
        User student = securityUtils.getCurrentUserCognitoSub();

        QuizAttempt attempt = quizAttemptRepository.findByAttemptCodeAndStudent(attemptCode, student)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found: " + attemptCode));

        List<QuizAttemptAnswer> answers =
                quizAttemptAnswerRepository.findByAttemptOrderByQuestionOrderAsc(attempt);

        Quiz quiz = attempt.getQuiz();
        Lesson lesson = quiz.getLesson();

        List<StudentQuizAttemptDetailResponse.AnswerDetail> answerDetails = answers.stream()
                .map(answer -> {
                    QuizQuestion question = answer.getQuestion();

                    return StudentQuizAttemptDetailResponse.AnswerDetail.builder()
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

        return StudentQuizAttemptDetailResponse.builder()
                .attemptCode(attempt.getAttemptCode())
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

    private String normalizeAnswer(String value) {
        if (value == null) return "";
        return value.trim().toUpperCase();
    }

    private StudentQuizAttemptHistoryResponse toAttemptHistoryResponse(QuizAttempt attempt) {
        Quiz quiz = attempt.getQuiz();
        Lesson lesson = quiz.getLesson();

        return StudentQuizAttemptHistoryResponse.builder()
                .attemptCode(attempt.getAttemptCode())
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
}
