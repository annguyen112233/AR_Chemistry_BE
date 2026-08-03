package com.chemistry.demo.services.quiz.impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.quiz.student.CompleteArRequest;
import com.chemistry.demo.dto.request.quiz.student.SaveQuizAnswerRequest;
import com.chemistry.demo.dto.response.quiz.student.*;
import com.chemistry.demo.entity.*;
import com.chemistry.demo.enums.*;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.QuizErrorCode;
import com.chemistry.demo.exception.ReactionErrorCode;
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

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentQuizServiceImpl implements StudentQuizService {
    private static final List<QuizAttemptStatus> COMPLETED_STATUSES =
            List.of(
                    QuizAttemptStatus.SUBMITTED,
                    QuizAttemptStatus.TIMEOUT
            );

    private static final List<QuizAttemptStatus> ACTIVE_STATUSES =
            List.of(
                    QuizAttemptStatus.WAITING_AR,
                    QuizAttemptStatus.RUNNING
            );

    private final ReactionDefinitionRepository reactionRepository;
    private final ReactionSubstanceRepository
            reactionSubstanceRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizQuestionOptionRepository quizQuestionOptionRepository;

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAttemptAnswerRepository quizAttemptAnswerRepository;

    private final SecurityUtils securityUtils;
    @Override
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @Transactional
    public PageResponse<StudentReactionResponse> getReactions(
            Integer grade,
            ReactionCategory reactionCategory,
            String keyword,
            Pageable pageable
    ) {
        validateGrade(grade);

        User student = securityUtils.getCurrentUserCognitoSub();

        String normalizedKeyword =
                keyword == null ? "" : keyword.trim();

        Page<ReactionDefinition> reactionPage =
                reactionRepository
                        .findByGradeAndReactionCategoryAndNameContainingIgnoreCaseAndActiveTrue(
                                grade,
                                reactionCategory,
                                normalizedKeyword,
                                pageable
                        );

        Set<String> completedReactionIds =
                quizAttemptRepository.findCompletedReactionIds(
                        student,
                        COMPLETED_STATUSES
                );

        List<StudentReactionResponse> content =
                reactionPage.getContent()
                        .stream()
                        .map(reaction -> mapReactionResponse(
                                reaction,
                                student,
                                completedReactionIds
                        ))
                        .toList();

        return PageResponse.<StudentReactionResponse>builder()
                .items(content)
                .page(reactionPage.getNumber())
                .size(reactionPage.getSize())
                .totalItems(reactionPage.getTotalElements())
                .totalPages(reactionPage.getTotalPages())
                .last(reactionPage.isLast())
                .build();
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @Transactional(readOnly = true)
    public StudentReactionDetailResponse getReactionDetail(
            String reactionId
    ) {
        ReactionDefinition reaction =
                getActiveReaction(reactionId);

        Quiz quiz =
                getPublishedQuiz(reaction);

        List<ReactionSubstance> reactants =
                reactionSubstanceRepository
                        .findByReactionAndRoleOrderBySubstanceOrderAsc(
                                reaction,
                                ReactionRole.REACTANT
                        );

        if (reactants.isEmpty() || reactants.size() > 2) {
            throw new AppException(
                    QuizErrorCode.INVALID_REACTION_CONFIGURATION
            );
        }

        List<StudentReactionDetailResponse.RequiredCardResponse>
                requiredCards =
                reactants.stream()
                        .map(reactant -> {
                            ChemicalSubstance substance =
                                    reactant.getSubstance();

                            return StudentReactionDetailResponse
                                    .RequiredCardResponse
                                    .builder()
                                    .formula(
                                            substance.getFormula()
                                    )
                                    .cardCode(
                                            substance.getFormula()
                                    )
                                    .reactantOrder(
                                            reactant.getSubstanceOrder()
                                    )
                                    .coefficient(
                                            reactant.getCoefficient()
                                    )
                                    .build();
                        })
                        .toList();

        return StudentReactionDetailResponse.builder()
                .reactionId(reaction.getId())
                .reactionName(reaction.getName())
                .equation(reaction.getEquation())
                .description(reaction.getDescription())
                .quizDuration(
                        quiz.getDurationSeconds()
                )
                .arSceneKey(
                        reaction.getArSceneKey().name()
                )
                .requiredCardCount(
                        requiredCards.size()
                )
                .requiredCards(requiredCards)
                .build();
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @Transactional(readOnly = true)
    public StudentQuizSummaryResponse getPublishedQuizByReaction(
            String reactionId
    ) {
        ReactionDefinition reaction =
                getActiveReaction(reactionId);

        Quiz quiz = getPublishedQuiz(reaction);

        long questionCount =
                quizQuestionRepository
                        .countByQuizAndStatus(
                                quiz,
                                QuizQuestionStatus.ACTIVE
                        );

        return StudentQuizSummaryResponse.builder()
                .quizCode(quiz.getQuizCode())
                .reactionId(reaction.getId())
                .reactionName(reaction.getName())
                .equation(reaction.getEquation())
                .grade(reaction.getGrade())
                .reactionType(
                        reaction.getReactionCategory().name()
                )
                .questionCount(questionCount)
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public StartQuizResponse startQuiz(
            String reactionId
    ) {
        User student = securityUtils.getCurrentUserCognitoSub();

        ReactionDefinition reaction =
                getActiveReaction(reactionId);

        Quiz quiz = getPublishedQuiz(reaction);

        Optional<QuizAttempt> existingOptional =
                quizAttemptRepository
                        .findFirstByStudentAndQuizAndStatusInOrderByCreatedAtDesc(
                                student,
                                quiz,
                                ACTIVE_STATUSES
                        );

        if (existingOptional.isPresent()) {
            QuizAttempt existing = existingOptional.get();

            finalizeIfExpired(existing);

            if (ACTIVE_STATUSES.contains(existing.getStatus())) {
                return StartQuizResponse.builder()
                        .attemptCode(existing.getAttemptCode())
                        .quizCode(quiz.getQuizCode())
                        .status(existing.getStatus())
                        .build();
            }
        }

        int totalQuestions = Math.toIntExact(
                quizQuestionRepository
                        .countByQuizAndStatus(
                                quiz,
                                QuizQuestionStatus.ACTIVE
                        )
        );

        if (totalQuestions != quiz.getQuestionLimit()) {
            throw new AppException(
                    QuizErrorCode.INVALID_QUIZ_CONFIGURATION
            );
        }

        QuizAttempt attempt = QuizAttempt.builder()
                .attemptCode(UUID.randomUUID().toString())
                .quiz(quiz)
                .student(student)
                .score(0)
                .correctCount(0)
                .totalQuestions(totalQuestions)
                .status(QuizAttemptStatus.WAITING_AR)
                .arCompleted(false)
                .quizDurationSeconds(quiz.getDurationSeconds())
                .startedAt(null)
                .expiredAt(null)
                .submittedAt(null)
                .abandonedAt(null)
                .build();

        quizAttemptRepository.save(attempt);

        return StartQuizResponse.builder()
                .attemptCode(attempt.getAttemptCode())
                .quizCode(quiz.getQuizCode())
                .status(attempt.getStatus())
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public CompleteArResponse completeAr(
            String attemptCode,
            CompleteArRequest request
    ) {
        User student = securityUtils.getCurrentUserCognitoSub();

        QuizAttempt attempt =
                getOwnedAttempt(attemptCode, student);

        /*
         * API idempotent:
         * Nếu Unity gọi lại thì không reset timer.
         */
        if (attempt.getStatus() == QuizAttemptStatus.RUNNING) {
            return buildCompleteArResponse(attempt);
        }

        if (attempt.getStatus() != QuizAttemptStatus.WAITING_AR) {
            throw new AppException(
                    QuizErrorCode.INVALID_QUIZ_ATTEMPT_STATUS
            );
        }

        if (!Boolean.TRUE.equals(request.getReactionSuccessful())) {
            throw new AppException(
                    QuizErrorCode.AR_REACTION_NOT_SUCCESSFUL
            );
        }

        validateScannedCards(
                attempt.getQuiz().getReaction(),
                request.getScannedCardCodes()
        );

        Instant now = Instant.now();

        attempt.setArCompleted(true);
        attempt.setStartedAt(now);
        attempt.setExpiredAt(
                now.plusSeconds(
                        attempt.getQuizDurationSeconds()
                )
        );
        attempt.setStatus(QuizAttemptStatus.RUNNING);

        quizAttemptRepository.save(attempt);

        return buildCompleteArResponse(attempt);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public QuizAttemptStateResponse getAttemptState(
            String attemptCode
    ) {
        User student = securityUtils.getCurrentUserCognitoSub();

        QuizAttempt attempt =
                getOwnedAttempt(attemptCode, student);

        finalizeIfExpired(attempt);

        return QuizAttemptStateResponse.builder()
                .attemptCode(attempt.getAttemptCode())
                .quizCode(attempt.getQuiz().getQuizCode())
                .reactionId(
                        attempt.getQuiz()
                                .getReaction()
                                .getId()
                )
                .status(attempt.getStatus())
                .arCompleted(attempt.getArCompleted())
                .quizUnlocked(
                        Boolean.TRUE.equals(
                                attempt.getArCompleted()
                        )
                )
                .startedAt(attempt.getStartedAt())
                .expiredAt(attempt.getExpiredAt())
                .remainingSeconds(
                        calculateRemainingSeconds(attempt)
                )
                .submitted(
                        COMPLETED_STATUSES.contains(
                                attempt.getStatus()
                        )
                )
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public StudentQuizContentResponse getQuizContent(
            String attemptCode
    ) {
        User student = securityUtils.getCurrentUserCognitoSub();

        QuizAttempt attempt =
                getOwnedAttempt(attemptCode, student);

        finalizeIfExpired(attempt);

        if (attempt.getStatus() != QuizAttemptStatus.RUNNING) {
            throw new AppException(
                    QuizErrorCode.QUIZ_CONTENT_LOCKED
            );
        }

        List<QuizQuestion> questions =
                quizQuestionRepository
                        .findByQuizAndStatusOrderByQuestionOrderAsc(
                                attempt.getQuiz(),
                                QuizQuestionStatus.ACTIVE
                        );

        Map<String, QuizAttemptAnswer> answerMap =
                quizAttemptAnswerRepository
                        .findByAttemptOrderByQuestionOrderAsc(attempt)
                        .stream()
                        .collect(Collectors.toMap(
                                answer ->
                                        answer.getQuestion().getId(),
                                Function.identity()
                        ));

        List<StudentQuizContentResponse.QuestionResponse>
                questionResponses =
                questions.stream()
                        .map(question ->
                                mapQuestionContent(
                                        question,
                                        answerMap.get(
                                                question.getId()
                                        )
                                )
                        )
                        .toList();

        ReactionDefinition reaction =
                attempt.getQuiz().getReaction();

        return StudentQuizContentResponse.builder()
                .attemptCode(attempt.getAttemptCode())
                .reactionName(reaction.getName())
                .equation(reaction.getEquation())
                .script(reaction.getScript())
                .remainingSeconds(
                        calculateRemainingSeconds(attempt)
                )
                .questions(questionResponses)
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public void saveAnswer(
            String attemptCode,
            String questionId,
            SaveQuizAnswerRequest request
    ) {
        User student = securityUtils.getCurrentUserCognitoSub();

        QuizAttempt attempt =
                getOwnedAttempt(attemptCode, student);

        finalizeIfExpired(attempt);

        validateRunningAttempt(attempt);

        QuizQuestion question =
                quizQuestionRepository
                        .findByIdAndQuiz(
                                questionId,
                                attempt.getQuiz()
                        )
                        .orElseThrow(() ->
                                new AppException(
                                        QuizErrorCode.QUESTION_NOT_FOUND
                                )
                        );

        String normalizedAnswer =
                normalizeAnswer(request.getAnswer());

        validateOption(
                question,
                normalizedAnswer
        );

        QuizAttemptAnswer attemptAnswer =
                quizAttemptAnswerRepository
                        .findByAttemptAndQuestion(
                                attempt,
                                question
                        )
                        .orElseGet(() ->
                                QuizAttemptAnswer.builder()
                                        .attempt(attempt)
                                        .question(question)
                                        .build()
                        );

        attemptAnswer.setStudentAnswer(normalizedAnswer);
        attemptAnswer.setAnsweredAt(Instant.now());

        // Chưa chấm trong lúc đang làm.
        attemptAnswer.setCorrect(null);
        attemptAnswer.setCorrectAnswer(null);
        attemptAnswer.setExplanation(null);
        attemptAnswer.setGradedAt(null);

        quizAttemptAnswerRepository.save(attemptAnswer);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public SubmitQuizResponse submitQuiz(
            String attemptCode
    ) {
        User student = securityUtils.getCurrentUserCognitoSub();

        QuizAttempt attempt =
                getOwnedAttempt(attemptCode, student);

        if (COMPLETED_STATUSES.contains(attempt.getStatus())) {
            return buildSubmitResponse(attempt);
        }

        validateRunningAttempt(attempt);

        QuizAttemptStatus finalStatus =
                isExpired(attempt)
                        ? QuizAttemptStatus.TIMEOUT
                        : QuizAttemptStatus.SUBMITTED;

        finalizeAttempt(
                attempt,
                finalStatus
        );

        return buildSubmitResponse(attempt);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @Transactional
    public StudentQuizAttemptDetailResponse
    getMyQuizAttemptDetail(
            String attemptCode
    ) {
        User student = securityUtils.getCurrentUserCognitoSub();

        QuizAttempt attempt =
                getOwnedAttempt(attemptCode, student);

        finalizeIfExpired(attempt);

        if (!COMPLETED_STATUSES.contains(
                attempt.getStatus()
        )) {
            throw new AppException(
                    QuizErrorCode.QUIZ_RESULT_NOT_AVAILABLE
            );
        }

        List<QuizAttemptAnswer> answers =
                quizAttemptAnswerRepository
                        .findByAttemptOrderByQuestionOrderAsc(
                                attempt
                        );

        Quiz quiz = attempt.getQuiz();
        ReactionDefinition reaction =
                quiz.getReaction();

        List<StudentQuizAttemptDetailResponse.AnswerDetail>
                answerDetails =
                answers.stream()
                        .map(answer ->
                                StudentQuizAttemptDetailResponse
                                        .AnswerDetail
                                        .builder()
                                        .questionId(
                                                answer.getQuestion()
                                                        .getId()
                                        )
                                        .questionOrder(
                                                answer.getQuestion()
                                                        .getQuestionOrder()
                                        )
                                        .questionText(
                                                answer.getQuestion()
                                                        .getQuestionText()
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
                                        .build()
                        )
                        .toList();

        return StudentQuizAttemptDetailResponse.builder()
                .attemptCode(attempt.getAttemptCode())
                .quizCode(quiz.getQuizCode())
                .quizTitle(quiz.getTitle())
                .reactionId(reaction.getId())
                .reactionName(reaction.getName())
                .equation(reaction.getEquation())
                .score(attempt.getScore())
                .totalQuestions(
                        attempt.getTotalQuestions()
                )
                .correctCount(
                        attempt.getCorrectCount()
                )
                .status(attempt.getStatus().name())
                .submittedAt(
                        attempt.getSubmittedAt()
                )
                .answers(answerDetails)
                .build();
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @Transactional
    public PageResponse<StudentQuizAttemptHistoryResponse>
    getReactionAttemptHistory(
            String reactionId,
            Pageable pageable
    ) {
        User student = securityUtils.getCurrentUserCognitoSub();

        ReactionDefinition reaction =
                getActiveReaction(reactionId);

        Page<QuizAttempt> attempts =
                quizAttemptRepository
                        .findByStudentAndQuizReactionAndStatusInOrderByCreatedAtDesc(
                                student,
                                reaction,
                                COMPLETED_STATUSES,
                                pageable
                        );

        return PageResponseUtils.toPageResponse(
                attempts,
                this::toAttemptHistoryResponse
        );
    }

    private StudentQuizAttemptHistoryResponse
    toAttemptHistoryResponse(
            QuizAttempt attempt
    ) {
        Quiz quiz = attempt.getQuiz();
        ReactionDefinition reaction =
                quiz.getReaction();

        return StudentQuizAttemptHistoryResponse.builder()
                .attemptCode(attempt.getAttemptCode())
                .quizCode(quiz.getQuizCode())
                .quizTitle(quiz.getTitle())
                .reactionId(reaction.getId())
                .reactionName(reaction.getName())
                .equation(reaction.getEquation())
                .score(attempt.getScore())
                .totalQuestions(
                        attempt.getTotalQuestions()
                )
                .correctCount(
                        attempt.getCorrectCount()
                )
                .status(attempt.getStatus().name())
                .submittedAt(
                        attempt.getSubmittedAt()
                )
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public void abandonAttempt(
            String attemptCode
    ) {
        User student = securityUtils.getCurrentUserCognitoSub();

        QuizAttempt attempt =
                getOwnedAttempt(attemptCode, student);

        if (COMPLETED_STATUSES.contains(
                attempt.getStatus()
        )) {
            throw new AppException(
                    QuizErrorCode.CANNOT_ABANDON_COMPLETED_ATTEMPT
            );
        }

        if (isExpired(attempt)) {
            finalizeAttempt(
                    attempt,
                    QuizAttemptStatus.TIMEOUT
            );
            return;
        }

        attempt.setStatus(
                QuizAttemptStatus.ABANDONED
        );
        attempt.setAbandonedAt(
                Instant.now()
        );

        quizAttemptRepository.save(attempt);
    }

    @Transactional
    public void finalizeAttempt(
            QuizAttempt attempt,
            QuizAttemptStatus finalStatus
    ) {
        if (COMPLETED_STATUSES.contains(attempt.getStatus())) {
            return;
        }

        List<QuizQuestion> questions =
                quizQuestionRepository
                        .findByQuizAndStatusOrderByQuestionOrderAsc(
                                attempt.getQuiz(),
                                QuizQuestionStatus.ACTIVE
                        );

        Map<String, QuizAttemptAnswer> answerMap =
                quizAttemptAnswerRepository
                        .findByAttemptOrderByQuestionOrderAsc(attempt)
                        .stream()
                        .collect(Collectors.toMap(
                                answer ->
                                        answer.getQuestion().getId(),
                                Function.identity()
                        ));

        int correctCount = 0;
        Instant gradedAt = Instant.now();

        List<QuizAttemptAnswer> gradedAnswers =
                new ArrayList<>();

        for (QuizQuestion question : questions) {
            QuizAttemptAnswer answer =
                    answerMap.get(question.getId());

            if (answer == null) {
                answer = QuizAttemptAnswer.builder()
                        .attempt(attempt)
                        .question(question)
                        .studentAnswer("")
                        .build();
            }

            String studentAnswer =
                    normalizeAnswer(
                            answer.getStudentAnswer()
                    );

            String correctAnswer =
                    normalizeAnswer(
                            question.getCorrectAnswer()
                    );

            boolean correct =
                    !studentAnswer.isBlank()
                            && studentAnswer.equals(
                            correctAnswer
                    );

            if (correct) {
                correctCount++;
            }

            answer.setStudentAnswer(studentAnswer);
            answer.setCorrectAnswer(
                    question.getCorrectAnswer()
            );
            answer.setCorrect(correct);
            answer.setExplanation(
                    question.getExplanation()
            );
            answer.setGradedAt(gradedAt);

            gradedAnswers.add(answer);
        }

        quizAttemptAnswerRepository.saveAll(
                gradedAnswers
        );

        attempt.setCorrectCount(correctCount);
        attempt.setScore(correctCount);
        attempt.setStatus(finalStatus);
        attempt.setSubmittedAt(gradedAt);

        quizAttemptRepository.save(attempt);
    }


    private SubmitQuizResponse buildSubmitResponse(
            QuizAttempt attempt
    ) {
        List<QuizAttemptAnswer> answers =
                quizAttemptAnswerRepository
                        .findByAttemptOrderByQuestionOrderAsc(
                                attempt
                        );

        List<SubmitQuizResponse.QuestionResult> results =
                answers.stream()
                        .map(answer ->
                                SubmitQuizResponse.QuestionResult
                                        .builder()
                                        .questionId(
                                                answer.getQuestion()
                                                        .getId()
                                        )
                                        .correct(
                                                answer.getCorrect()
                                        )
                                        .studentAnswer(
                                                answer.getStudentAnswer()
                                        )
                                        .correctAnswer(
                                                answer.getCorrectAnswer()
                                        )
                                        .explanation(
                                                answer.getExplanation()
                                        )
                                        .build()
                        )
                        .toList();

        return SubmitQuizResponse.builder()
                .attemptCode(attempt.getAttemptCode())
                .score(attempt.getScore())
                .total(attempt.getTotalQuestions())
                .correctCount(
                        attempt.getCorrectCount()
                )
                .results(results)
                .build();
    }
    private void validateOption(
            QuizQuestion question,
            String answer
    ) {
        boolean exists =
                quizQuestionOptionRepository
                        .findByQuestionAndOptionKeyIgnoreCase(
                                question,
                                answer
                        )
                        .isPresent();

        if (!exists) {
            throw new AppException(
                    QuizErrorCode.INVALID_QUESTION_OPTION
            );
        }
    }

    private StudentQuizContentResponse.QuestionResponse
    mapQuestionContent(
            QuizQuestion question,
            QuizAttemptAnswer savedAnswer
    ) {
        List<StudentQuizContentResponse.OptionResponse> options =
                quizQuestionOptionRepository
                        .findByQuestionOrderByOptionOrderAsc(
                                question
                        )
                        .stream()
                        .map(option ->
                                StudentQuizContentResponse.OptionResponse
                                        .builder()
                                        .optionKey(
                                                option.getOptionKey()
                                        )
                                        .optionText(
                                                option.getOptionText()
                                        )
                                        .optionOrder(
                                                option.getOptionOrder()
                                        )
                                        .build()
                        )
                        .toList();

        return StudentQuizContentResponse.QuestionResponse
                .builder()
                .questionId(question.getId())
                .questionOrder(question.getQuestionOrder())
                .questionText(question.getQuestionText())
                .selectedAnswer(
                        savedAnswer == null
                                ? null
                                : savedAnswer.getStudentAnswer()
                )
                .options(options)
                .build();
    }

    private StudentReactionResponse mapReactionResponse(
            ReactionDefinition reaction,
            User student,
            Set<String> completedReactionIds
    ) {
        Optional<QuizAttempt> activeAttemptOptional =
                quizAttemptRepository
                        .findFirstByStudentAndQuizReactionAndStatusInOrderByCreatedAtDesc(
                                student,
                                reaction,
                                ACTIVE_STATUSES
                        );

        QuizAttempt activeAttempt = activeAttemptOptional.orElse(null);

        if (activeAttempt != null) {
            finalizeIfExpired(activeAttempt);
        }

        boolean stillActive =
                activeAttempt != null
                        && ACTIVE_STATUSES.contains(activeAttempt.getStatus());

        Optional<QuizAttempt> latestCompleted =
                quizAttemptRepository
                        .findFirstByStudentAndQuizReactionAndStatusInOrderByCreatedAtDesc(
                                student,
                                reaction,
                                COMPLETED_STATUSES
                        );

        boolean completed =
                completedReactionIds.contains(reaction.getId());

        return StudentReactionResponse.builder()
                .reactionId(reaction.getId())
                .reactionName(reaction.getName())
                .reactionCode(reaction.getCode())
                .equation(reaction.getEquation())
                .grade(reaction.getGrade())
                .reactionCategory(reaction.getReactionCategory().name())
                .completed(completed)
                .hasRunningAttempt(stillActive)
                .activeAttemptCode(
                        stillActive
                                ? activeAttempt.getAttemptCode()
                                : null
                )
                .remainingSeconds(
                        stillActive
                                ? calculateRemainingSeconds(activeAttempt)
                                : null
                )
                .latestCompletedAttemptCode(
                        latestCompleted
                                .map(QuizAttempt::getAttemptCode)
                                .orElse(null)
                )
                .canStart(!stillActive)
                .canContinue(stillActive)
                .canViewHistory(completed)
                .canRetry(completed && !stillActive)
                .build();
    }

    private CompleteArResponse buildCompleteArResponse(
            QuizAttempt attempt
    ) {
        return CompleteArResponse.builder()
                .attemptCode(attempt.getAttemptCode())
                .status(attempt.getStatus())
                .startedAt(attempt.getStartedAt())
                .expiredAt(attempt.getExpiredAt())
                .remainingSeconds(
                        calculateRemainingSeconds(attempt)
                )
                .build();
    }

    private void validateScannedCards(
            ReactionDefinition reaction,
            List<String> scannedCardCodes
    ) {
        List<ReactionSubstance> requiredReactants =
                reactionSubstanceRepository
                        .findByReactionAndRoleOrderBySubstanceOrderAsc(
                                reaction,
                                ReactionRole.REACTANT
                        );

        if (requiredReactants.isEmpty()
                || requiredReactants.size() > 2) {
            throw new AppException(
                    QuizErrorCode.INVALID_REACTION_CONFIGURATION
            );
        }

        if (scannedCardCodes == null
                || scannedCardCodes.isEmpty()) {
            throw new AppException(
                    QuizErrorCode.INVALID_AR_FLASH_CARDS
            );
        }

        List<String> normalizedInput =
                scannedCardCodes.stream()
                        .filter(Objects::nonNull)
                        .map(this::normalizeCardCode)
                        .filter(code -> !code.isBlank())
                        .toList();

        Set<String> scannedCodes =
                new HashSet<>(normalizedInput);

        /*
         * Phát hiện quét trùng card.
         */
        if (scannedCodes.size()
                != normalizedInput.size()) {
            throw new AppException(
                    QuizErrorCode.DUPLICATE_FLASH_CARD
            );
        }

        Set<String> requiredCodes =
                requiredReactants.stream()
                        .map(ReactionSubstance::getSubstance)
                        .map(ChemicalSubstance::getFormula)
                        .map(this::normalizeCardCode)
                        .collect(Collectors.toSet());

        if (!requiredCodes.equals(scannedCodes)) {
            throw new AppException(
                    QuizErrorCode.INVALID_AR_FLASH_CARDS
            );
        }
    }

    private String normalizeCardCode(String code) {
        if (code == null) {
            return "";
        }

        return code.trim().toUpperCase(Locale.ROOT);
    }

    private ReactionDefinition getActiveReaction(
            String reactionId
    ) {
        ReactionDefinition reaction =
                reactionRepository
                        .findById(reactionId)
                        .orElseThrow(() ->
                                new AppException(
                                        ReactionErrorCode.REACTION_NOT_FOUND,
                                        reactionId
                                )
                        );

        if (!Boolean.TRUE.equals(
                reaction.getActive()
        )) {
            throw new AppException(
                    ReactionErrorCode.REACTION_NOT_FOUND,
                    reactionId
            );
        }

        return reaction;
    }

    private Quiz getPublishedQuiz(
            ReactionDefinition reaction
    ) {
        return quizRepository
                .findTopByReactionAndStatusOrderByCreatedAtDesc(
                        reaction,
                        QuizStatus.PUBLISHED
                )
                .orElseThrow(() ->
                        new AppException(
                                QuizErrorCode.QUIZ_NOT_FOUND
                        )
                );
    }

    private QuizAttempt getOwnedAttempt(
            String attemptCode,
            User student
    ) {
        return quizAttemptRepository
                .findByAttemptCodeAndStudent(
                        attemptCode,
                        student
                )
                .orElseThrow(() ->
                        new AppException(
                                QuizErrorCode.QUIZ_ATTEMPT_NOT_FOUND
                        )
                );
    }

    private void validateRunningAttempt(
            QuizAttempt attempt
    ) {
        if (attempt.getStatus()
                != QuizAttemptStatus.RUNNING) {
            throw new AppException(
                    QuizErrorCode.INVALID_QUIZ_ATTEMPT_STATUS
            );
        }

        if (!Boolean.TRUE.equals(
                attempt.getArCompleted()
        )) {
            throw new AppException(
                    QuizErrorCode.AR_NOT_COMPLETED
            );
        }
    }

    private boolean isExpired(
            QuizAttempt attempt
    ) {
        return attempt.getStatus()
                == QuizAttemptStatus.RUNNING
                && attempt.getExpiredAt() != null
                && !Instant.now()
                .isBefore(attempt.getExpiredAt());
    }

    private void finalizeIfExpired(
            QuizAttempt attempt
    ) {
        if (isExpired(attempt)) {
            finalizeAttempt(
                    attempt,
                    QuizAttemptStatus.TIMEOUT
            );
        }
    }

    private long calculateRemainingSeconds(
            QuizAttempt attempt
    ) {
        if (attempt.getStatus()
                == QuizAttemptStatus.WAITING_AR) {
            return attempt.getQuizDurationSeconds();
        }

        if (attempt.getExpiredAt() == null) {
            return 0;
        }

        return Math.max(
                0,
                Duration.between(
                        Instant.now(),
                        attempt.getExpiredAt()
                ).getSeconds()
        );
    }

    private String normalizeAnswer(
            String value
    ) {
        if (value == null) {
            return "";
        }

        return value.trim()
                .toUpperCase(Locale.ROOT);
    }

    private void validateGrade(
            Integer grade
    ) {
        if (grade == null
                || grade < 8
                || grade > 12) {
            throw new AppException(
                    QuizErrorCode.INVALID_GRADE
            );
        }
    }
}