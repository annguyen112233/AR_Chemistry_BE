package com.chemistry.demo.services.quiz.impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffLessonQuizOverviewResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffQuizDetailResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffQuizQuestionResponse;
import com.chemistry.demo.dto.response.quiz.staff.StaffQuizSummaryResponse;
import com.chemistry.demo.entity.Lesson;
import com.chemistry.demo.entity.Quiz;
import com.chemistry.demo.entity.QuizQuestion;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.LessonErrorCode;
import com.chemistry.demo.exception.QuizErrorCode;
import com.chemistry.demo.repository.LessonRepository;
import com.chemistry.demo.repository.QuizQuestionRepository;
import com.chemistry.demo.repository.QuizRepository;
import com.chemistry.demo.services.quiz.StaffQuizManagementService;
import com.chemistry.demo.utils.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StaffQuizManagementServiceImpl implements StaffQuizManagementService {
    private final LessonRepository lessonRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")
    public PageResponse<StaffLessonQuizOverviewResponse> getLessonQuizOverview(Pageable pageable) {
        Page<Lesson> lessons = lessonRepository.findAllByOrderByLessonNumberAsc(pageable);

        return PageResponseUtils.toPageResponse(
                lessons,
                this::toOverviewResponse
        );
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")
    public PageResponse<StaffQuizSummaryResponse> getQuizzesByLesson(String lessonCode, Pageable pageable) {
        Lesson lesson = lessonRepository.findByLessonCode(lessonCode)
                .orElseThrow(() -> new AppException(LessonErrorCode.LESSON_NOT_FOUND));

        Page<Quiz> quizzes = quizRepository.findByLessonOrderByCreatedAtDesc(lesson, pageable);

        return PageResponseUtils.toPageResponse(
                quizzes,
                quiz -> StaffQuizSummaryResponse.builder()
                        .quizCode(quiz.getQuizCode())
                        .title(quiz.getTitle())
                        .status(quiz.getStatus())
                        .generatedBy(quiz.getGeneratedBy())
                        .version(quiz.getVersion())
                        .questionCount(quizQuestionRepository.countByQuiz(quiz))
                        .build()
        );
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")
    public StaffQuizDetailResponse getQuizDetail(String quizCode, Pageable pageable) {
        Quiz quiz = quizRepository.findByQuizCode(quizCode)
                .orElseThrow(() -> new AppException(QuizErrorCode.QUIZ_NOT_FOUND));

        Page<QuizQuestion> quizQuestions =
                quizQuestionRepository.findByQuizOrderByQuestionOrderAsc(quiz, pageable);

        PageResponse<StaffQuizQuestionResponse> questionPage =
                PageResponseUtils.toPageResponse(
                        quizQuestions,
                        q -> StaffQuizQuestionResponse.builder()
                                .id(q.getId())
                                .questionOrder(q.getQuestionOrder())
                                .type(q.getType())
                                .questionText(q.getQuestionText())
                                .optionsJson(q.getOptionsJson())
                                .correctAnswer(q.getCorrectAnswer())
                                .explanation(q.getExplanation())
                                .difficulty(q.getDifficulty())
                                .status(q.getStatus())
                                .build()
                );

        return StaffQuizDetailResponse.builder()
                .quizCode(quiz.getQuizCode())
                .lessonCode(quiz.getLesson().getLessonCode())
                .title(quiz.getTitle())
                .status(quiz.getStatus())
                .generatedBy(quiz.getGeneratedBy())
                .version(quiz.getVersion())
                .questions(questionPage)
                .build();
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')")
    @Transactional
    public StaffQuizSummaryResponse publishQuiz(String quizCode) {
        Quiz quiz = quizRepository.findByQuizCode(quizCode)
                .orElseThrow(() -> new AppException(QuizErrorCode.QUIZ_NOT_FOUND));

        long questionCount = quizQuestionRepository.countByQuiz(quiz);

        if (questionCount <= 0) {
            throw new AppException(QuizErrorCode.QUIZ_HAS_NO_QUESTIONS);
        }

        if ("published".equalsIgnoreCase(quiz.getStatus())) {
            throw new AppException(QuizErrorCode.QUIZ_ALREADY_PUBLISHED);
        }

        quiz.setStatus("published");
        quizRepository.save(quiz);

        return StaffQuizSummaryResponse.builder()
                .quizCode(quiz.getQuizCode())
                .title(quiz.getTitle())
                .status(quiz.getStatus())
                .generatedBy(quiz.getGeneratedBy())
                .version(quiz.getVersion())
                .questionCount(questionCount)
                .build();
    }

    private StaffLessonQuizOverviewResponse toOverviewResponse(Lesson lesson) {
        Quiz latestQuiz = quizRepository.findTopByLessonOrderByCreatedAtDesc(lesson)
                .orElse(null);

        boolean hasQuiz = latestQuiz != null;

        Long questionCount = hasQuiz
                ? quizQuestionRepository.countByQuiz(latestQuiz)
                : 0L;

        return StaffLessonQuizOverviewResponse.builder()
                .lessonCode(lesson.getLessonCode())
                .lessonNumber(lesson.getLessonNumber())
                .lessonTitle(lesson.getTitle())
                .chapter(lesson.getChapter())
                .hasQuiz(hasQuiz)
                .latestQuizCode(hasQuiz ? latestQuiz.getQuizCode() : null)
                .latestQuizTitle(hasQuiz ? latestQuiz.getTitle() : null)
                .latestQuizStatus(hasQuiz ? latestQuiz.getStatus() : null)
                .latestQuizVersion(hasQuiz ? latestQuiz.getVersion() : null)
                .questionCount(questionCount)
                .build();
    }
}
