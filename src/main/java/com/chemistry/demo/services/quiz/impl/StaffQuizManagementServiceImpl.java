package com.chemistry.demo.services.quiz.impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.quiz.staff.*;
import com.chemistry.demo.entity.Quiz;
import com.chemistry.demo.entity.QuizQuestion;
import com.chemistry.demo.entity.QuizQuestionOption;
import com.chemistry.demo.entity.ReactionDefinition;
import com.chemistry.demo.enums.QuizStatus;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.QuizErrorCode;
import com.chemistry.demo.exception.ReactionErrorCode;
import com.chemistry.demo.repository.QuizQuestionOptionRepository;
import com.chemistry.demo.repository.QuizQuestionRepository;
import com.chemistry.demo.repository.QuizRepository;
import com.chemistry.demo.repository.ReactionDefinitionRepository;
import com.chemistry.demo.services.quiz.StaffQuizManagementService;
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
public class StaffQuizManagementServiceImpl
        implements StaffQuizManagementService {

    private final ReactionDefinitionRepository
            reactionDefinitionRepository;

    private final QuizRepository quizRepository;

    private final QuizQuestionRepository
            quizQuestionRepository;

    private final QuizQuestionOptionRepository
            quizQuestionOptionRepository;

    @Override
    @PreAuthorize(
            "hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')"
    )
    @Transactional(readOnly = true)
    public PageResponse<StaffReactionQuizOverviewResponse>
    getReactionQuizOverview(
            Pageable pageable
    ) {
        Page<ReactionDefinition> reactions =
                reactionDefinitionRepository
                        .findAll(pageable);

        return PageResponseUtils.toPageResponse(
                reactions,
                this::toOverviewResponse
        );
    }

    @Override
    @PreAuthorize(
            "hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')"
    )
    @Transactional(readOnly = true)
    public PageResponse<StaffQuizSummaryResponse>
    getQuizzesByReaction(
            String reactionCode,
            Pageable pageable
    ) {
        ReactionDefinition reaction =
                reactionDefinitionRepository
                        .findByCode(
                                normalizeCode(
                                        reactionCode
                                )
                        )
                        .orElseThrow(() ->
                                new AppException(
                                        ReactionErrorCode.REACTION_NOT_FOUND,
                                        reactionCode
                                )
                        );

        Page<Quiz> quizzes =
                quizRepository
                        .findByReactionOrderByCreatedAtDesc(
                                reaction,
                                pageable
                        );

        return PageResponseUtils.toPageResponse(
                quizzes,
                this::toQuizSummaryResponse
        );
    }

    @Override
    @PreAuthorize(
            "hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')"
    )
    @Transactional(readOnly = true)
    public StaffQuizDetailResponse getQuizDetail(
            String quizCode,
            Pageable pageable
    ) {
        Quiz quiz =
                quizRepository
                        .findByQuizCode(
                                quizCode.trim()
                        )
                        .orElseThrow(() ->
                                new AppException(
                                        QuizErrorCode.QUIZ_NOT_FOUND
                                )
                        );

        Page<QuizQuestion> quizQuestions =
                quizQuestionRepository
                        .findByQuizOrderByQuestionOrderAsc(
                                quiz,
                                pageable
                        );

        PageResponse<StaffQuizQuestionResponse>
                questionPage =
                PageResponseUtils.toPageResponse(
                        quizQuestions,
                        this::toQuestionResponse
                );

        ReactionDefinition reaction =
                quiz.getReaction();

        return StaffQuizDetailResponse.builder()
                .quizCode(quiz.getQuizCode())
                .reactionId(reaction.getId())
                .reactionCode(reaction.getCode())
                .reactionName(reaction.getName())
                .equation(reaction.getEquation())
                .grade(reaction.getGrade())
                .reactionCategory(
                        reaction
                                .getReactionCategory()
                                .name()
                )
                .title(quiz.getTitle())
                .status(quiz.getStatus())
                .generatedBy(
                        quiz.getGeneratedBy()
                )
                .version(quiz.getVersion())
                .questionLimit(
                        quiz.getQuestionLimit()
                )
                .durationSeconds(
                        quiz.getDurationSeconds()
                )
                .questions(questionPage)
                .build();
    }

    @Override
    @PreAuthorize(
            "hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')"
    )
    @Transactional
    public StaffQuizSummaryResponse publishQuiz(
            String quizCode
    ) {
        Quiz quiz =
                quizRepository
                        .findByQuizCode(
                                quizCode.trim()
                        )
                        .orElseThrow(() ->
                                new AppException(
                                        QuizErrorCode.QUIZ_NOT_FOUND
                                )
                        );

        if (quiz.getStatus()
                == QuizStatus.PUBLISHED) {
            throw new AppException(
                    QuizErrorCode.QUIZ_ALREADY_PUBLISHED
            );
        }

        if (quiz.getStatus()
                != QuizStatus.READY) {
            throw new AppException(
                    QuizErrorCode.INVALID_QUIZ_CONFIGURATION
            );
        }

        long questionCount =
                quizQuestionRepository
                        .countByQuiz(quiz);

        if (questionCount
                != quiz.getQuestionLimit()) {
            throw new AppException(
                    QuizErrorCode.INVALID_QUIZ_CONFIGURATION
            );
        }

        validateQuestionOptions(quiz);

        List<Quiz> publishedQuizzes =
                quizRepository
                        .findByReactionAndStatus(
                                quiz.getReaction(),
                                QuizStatus.PUBLISHED
                        );

        for (Quiz publishedQuiz
                : publishedQuizzes) {
            if (!publishedQuiz.getId()
                    .equals(quiz.getId())) {
                publishedQuiz.setStatus(
                        QuizStatus.ARCHIVED
                );
            }
        }

        if (!publishedQuizzes.isEmpty()) {
            quizRepository.saveAll(
                    publishedQuizzes
            );
        }

        quiz.setStatus(
                QuizStatus.PUBLISHED
        );

        Quiz savedQuiz =
                quizRepository.save(quiz);

        return toQuizSummaryResponse(
                savedQuiz
        );
    }

    private void validateQuestionOptions(
            Quiz quiz
    ) {
        List<QuizQuestion> questions =
                quizQuestionRepository
                        .findByQuizOrderByQuestionOrderAsc(
                                quiz
                        );

        if (questions.size()
                != quiz.getQuestionLimit()) {
            throw new AppException(
                    QuizErrorCode.INVALID_QUIZ_CONFIGURATION
            );
        }

        for (QuizQuestion question
                : questions) {
            List<QuizQuestionOption> options =
                    quizQuestionOptionRepository
                            .findByQuestionOrderByOptionOrderAsc(
                                    question
                            );

            if (options.size() != 4) {
                throw new AppException(
                        QuizErrorCode.INVALID_QUIZ_CONFIGURATION
                );
            }

            boolean correctAnswerExists =
                    options.stream()
                            .anyMatch(option ->
                                    option.getOptionKey()
                                            .equalsIgnoreCase(
                                                    question
                                                            .getCorrectAnswer()
                                            )
                            );

            if (!correctAnswerExists) {
                throw new AppException(
                        QuizErrorCode.INVALID_QUIZ_CONFIGURATION
                );
            }
        }
    }

    private StaffReactionQuizOverviewResponse
    toOverviewResponse(
            ReactionDefinition reaction
    ) {
        Quiz latestQuiz =
                quizRepository
                        .findTopByReactionOrderByCreatedAtDesc(
                                reaction
                        )
                        .orElse(null);

        boolean hasQuiz =
                latestQuiz != null;

        long questionCount =
                hasQuiz
                        ? quizQuestionRepository
                        .countByQuiz(latestQuiz)
                        : 0L;

        return StaffReactionQuizOverviewResponse
                .builder()
                .reactionId(reaction.getId())
                .reactionCode(reaction.getCode())
                .reactionName(reaction.getName())
                .equation(reaction.getEquation())
                .grade(reaction.getGrade())
                .reactionCategory(
                        reaction
                                .getReactionCategory()
                                .name()
                )
                .reactionType(
                        reaction
                                .getReactionType()
                                .name()
                )
                .active(reaction.getActive())
                .hasQuiz(hasQuiz)
                .latestQuizCode(
                        hasQuiz
                                ? latestQuiz.getQuizCode()
                                : null
                )
                .latestQuizTitle(
                        hasQuiz
                                ? latestQuiz.getTitle()
                                : null
                )
                .latestQuizStatus(
                        hasQuiz
                                ? latestQuiz.getStatus()
                                : null
                )
                .latestQuizVersion(
                        hasQuiz
                                ? latestQuiz.getVersion()
                                : null
                )
                .questionCount(
                        questionCount
                )
                .build();
    }

    private StaffQuizSummaryResponse
    toQuizSummaryResponse(
            Quiz quiz
    ) {
        ReactionDefinition reaction =
                quiz.getReaction();

        return StaffQuizSummaryResponse.builder()
                .quizCode(quiz.getQuizCode())
                .reactionId(reaction.getId())
                .reactionCode(reaction.getCode())
                .reactionName(reaction.getName())
                .title(quiz.getTitle())
                .status(quiz.getStatus())
                .generatedBy(
                        quiz.getGeneratedBy()
                )
                .version(quiz.getVersion())
                .durationSeconds(
                        quiz.getDurationSeconds()
                )
                .questionLimit(
                        quiz.getQuestionLimit()
                )
                .questionCount(
                        quizQuestionRepository
                                .countByQuiz(quiz)
                )
                .build();
    }

    private StaffQuizQuestionResponse
    toQuestionResponse(
            QuizQuestion question
    ) {
        List<StaffQuizOptionResponse> options =
                quizQuestionOptionRepository
                        .findByQuestionOrderByOptionOrderAsc(
                                question
                        )
                        .stream()
                        .map(this::toOptionResponse)
                        .toList();

        return StaffQuizQuestionResponse.builder()
                .id(question.getId())
                .questionOrder(
                        question.getQuestionOrder()
                )
                .questionText(
                        question.getQuestionText()
                )
                .options(options)
                .correctAnswer(
                        question.getCorrectAnswer()
                )
                .explanation(
                        question.getExplanation()
                )
                .status(
                        question.getStatus()
                )
                .build();
    }

    private StaffQuizOptionResponse
    toOptionResponse(
            QuizQuestionOption option
    ) {
        return StaffQuizOptionResponse.builder()
                .id(option.getId())
                .optionKey(
                        option.getOptionKey()
                )
                .optionText(
                        option.getOptionText()
                )
                .optionOrder(
                        option.getOptionOrder()
                )
                .build();
    }

    private String normalizeCode(
            String code
    ) {
        if (code == null
                || code.isBlank()) {
            throw new AppException(
                    ReactionErrorCode.REACTION_NOT_FOUND,
                    code
            );
        }

        return code
                .trim()
                .toUpperCase();
    }
}