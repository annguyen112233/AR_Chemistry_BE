package com.chemistry.demo.services.quiz.impl;

import com.chemistry.demo.dto.request.quiz.student.SubmitQuizRequest;
import com.chemistry.demo.dto.response.quiz.student.*;
import com.chemistry.demo.entity.Lesson;
import com.chemistry.demo.entity.Quiz;
import com.chemistry.demo.entity.QuizQuestion;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.LessonErrorCode;
import com.chemistry.demo.repository.LessonRepository;
import com.chemistry.demo.repository.QuizQuestionRepository;
import com.chemistry.demo.repository.QuizRepository;
import com.chemistry.demo.services.quiz.StudentQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentQuizServiceImpl implements StudentQuizService {

    private static final String PUBLISHED = "published";
    private static final String ACTIVE = "active";

    private final LessonRepository lessonRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    @Override
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public StudentQuizSummaryResponse getPublishedQuizByLesson(String lessonCode) {
        Lesson lesson = lessonRepository.findByLessonCode(lessonCode)
                .orElseThrow(() -> new AppException(LessonErrorCode.LESSON_NOT_FOUND));

        Quiz quiz = quizRepository.findTopByLessonAndStatusOrderByCreatedAtDesc(lesson, PUBLISHED)
                .orElseThrow(() -> new RuntimeException("Published quiz not found for lesson: " + lessonCode));

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
                .orElseThrow(() -> new RuntimeException("Published quiz not found: " + quizCode));

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
    public SubmitQuizResponse submitQuiz(String quizCode, SubmitQuizRequest request) {
        Quiz quiz = quizRepository.findByQuizCodeAndStatus(quizCode, PUBLISHED)
                .orElseThrow(() -> new RuntimeException("Published quiz not found: " + quizCode));

        List<QuizQuestion> questions =
                quizQuestionRepository.findByQuizAndStatusOrderByQuestionOrderAsc(quiz, ACTIVE);

        Map<String, QuizQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(QuizQuestion::getId, q -> q));

        Map<String, String> answerMap = Optional.ofNullable(request.getAnswers())
                .orElse(List.of())
                .stream()
                .collect(Collectors.toMap(
                        SubmitQuizRequest.AnswerRequest::getQuestionId,
                        a -> normalizeAnswer(a.getAnswer()),
                        (a, b) -> a
                ));

        List<SubmitQuizResponse.QuestionResult> results = new ArrayList<>();
        int correctCount = 0;

        for (QuizQuestion question : questions) {
            String questionId = question.getId();
            String studentAnswer = answerMap.getOrDefault(questionId, "");
            String correctAnswer = normalizeAnswer(question.getCorrectAnswer());

            boolean correct = studentAnswer.equals(correctAnswer);

            if (correct) {
                correctCount++;
            }

            results.add(SubmitQuizResponse.QuestionResult.builder()
                    .questionId(questionId)
                    .correct(correct)
                    .studentAnswer(studentAnswer)
                    .correctAnswer(question.getCorrectAnswer())
                    .explanation(question.getExplanation())
                    .build());
        }

        return SubmitQuizResponse.builder()
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

    private String normalizeAnswer(String value) {
        if (value == null) return "";
        return value.trim().toUpperCase();
    }
}
