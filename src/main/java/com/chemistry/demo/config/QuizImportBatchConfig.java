package com.chemistry.demo.config;

import com.chemistry.demo.dto.quizCSV.QuizCsvRow;
import com.chemistry.demo.entity.*;
import com.chemistry.demo.enums.QuizImportStatus;
import com.chemistry.demo.enums.QuizQuestionStatus;
import com.chemistry.demo.enums.QuizStatus;
import com.chemistry.demo.repository.*;
import com.chemistry.demo.services.quiz.QuizCsvS3ReaderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuizImportBatchConfig {

    private static final int REQUIRED_QUESTION_COUNT = 5;

    private static final int QUIZ_DURATION_SECONDS = 420;

    @Bean
    public Job importQuizJob(
            JobRepository jobRepository,
            Step importQuizStep
    ) {
        return new JobBuilder(
                "importQuizJob",
                jobRepository
        )
                .start(importQuizStep)
                .build();
    }

    @Bean
    public Step importQuizStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            QuizCsvS3ReaderService quizCsvS3ReaderService,
            QuizImportJobRepository quizImportJobRepository,
            ReactionDefinitionRepository reactionDefinitionRepository,
            QuizRepository quizRepository,
            QuizQuestionRepository quizQuestionRepository,
            QuizQuestionOptionRepository quizQuestionOptionRepository
    ) {
        return new StepBuilder(
                "importQuizStep",
                jobRepository
        )
                .tasklet((contribution, chunkContext) -> {

                    String jobCode =
                            getStringJobParameter(
                                    chunkContext,
                                    "jobCode"
                            );

                    String reactionCode =
                            getStringJobParameter(
                                    chunkContext,
                                    "reactionCode"
                            );

                    String s3Key =
                            getStringJobParameter(
                                    chunkContext,
                                    "s3Key"
                            );

                    QuizImportJob importJob =
                            quizImportJobRepository
                                    .findByJobCode(jobCode)
                                    .orElseThrow(() ->
                                            new IllegalStateException(
                                                    "Import job not found: "
                                                            + jobCode
                                            )
                                    );

                    try {
                        importJob.setStatus(
                                QuizImportStatus.PROCESSING
                        );
                        importJob.setErrorMessage(null);

                        quizImportJobRepository.save(
                                importJob
                        );

                        ReactionDefinition reaction =
                                reactionDefinitionRepository
                                        .findByCode(
                                                reactionCode
                                                        .trim()
                                                        .toUpperCase()
                                        )
                                        .orElseThrow(() ->
                                                new IllegalStateException(
                                                        "Reaction not found: "
                                                                + reactionCode
                                                )
                                        );

                        if (!Boolean.TRUE.equals(
                                reaction.getActive()
                        )) {
                            throw new IllegalStateException(
                                    "Reaction is inactive: "
                                            + reaction.getCode()
                            );
                        }

                        List<QuizCsvRow> rows =
                                quizCsvS3ReaderService
                                        .readCsvFromS3(s3Key);

                        /*
                         * Không được import CSV rỗng.
                         */
                        if (rows == null || rows.isEmpty()) {
                            throw new IllegalStateException(
                                    "CSV has no question rows"
                            );
                        }

                        /*
                         * Quiz của student yêu cầu đúng 5 câu.
                         */
                        if (rows.size()
                                != REQUIRED_QUESTION_COUNT) {
                            throw new IllegalStateException(
                                    "CSV must contain exactly "
                                            + REQUIRED_QUESTION_COUNT
                                            + " questions, but found "
                                            + rows.size()
                            );
                        }

                        /*
                         * Phải validate toàn bộ trước khi tạo Quiz.
                         * Chỉ một dòng sai thì import thất bại toàn bộ.
                         */
                        validateAllRows(rows);

                        QuizCsvRow firstRow =
                                rows.getFirst();

                        int nextVersion =
                                resolveNextVersion(
                                        quizRepository,
                                        reaction
                                );

                        String quizCode =
                                buildQuizCode(
                                        reaction.getCode(),
                                        nextVersion
                                );

                        Quiz quiz = new Quiz();

                        quiz.setReaction(reaction);
                        quiz.setQuizCode(quizCode);
                        quiz.setTitle(
                                firstRow.getQuizTitle().trim()
                        );
                        quiz.setGeneratedBy(
                                "EXTERNAL_AI_CSV"
                        );
                        quiz.setStatus(
                                QuizStatus.READY
                        );
                        quiz.setVersion(nextVersion);
                        quiz.setQuestionLimit(
                                REQUIRED_QUESTION_COUNT
                        );
                        quiz.setDurationSeconds(
                                QUIZ_DURATION_SECONDS
                        );
                        quiz.setImportJobCode(
                                importJob.getJobCode()
                        );

                        Quiz savedQuiz =
                                quizRepository.save(quiz);

                        for (QuizCsvRow row : rows) {
                            QuizQuestion question =
                                    mapToQuestion(
                                            row,
                                            savedQuiz
                                    );

                            QuizQuestion savedQuestion =
                                    quizQuestionRepository.save(
                                            question
                                    );

                            saveQuestionOptions(
                                    row,
                                    savedQuestion,
                                    quizQuestionOptionRepository
                            );

                            log.info(
                                    "Imported quiz question. quizCode={}, questionOrder={}",
                                    savedQuiz.getQuizCode(),
                                    row.getQuestionOrder()
                            );
                        }

                        importJob.setQuiz(savedQuiz);
                        importJob.setTotalRows(rows.size());
                        importJob.setSuccessRows(rows.size());
                        importJob.setFailedRows(0);
                        importJob.setStatus(
                                QuizImportStatus.COMPLETED
                        );
                        importJob.setErrorMessage(null);

                        quizImportJobRepository.save(
                                importJob
                        );

                        log.info(
                                "Quiz import completed. jobCode={}, reactionCode={}, quizCode={}",
                                jobCode,
                                reaction.getCode(),
                                savedQuiz.getQuizCode()
                        );

                        return RepeatStatus.FINISHED;

                    } catch (Exception exception) {
                        importJob.setStatus(
                                QuizImportStatus.FAILED
                        );

                        importJob.setFailedRows(
                                importJob.getTotalRows() != null
                                        ? importJob.getTotalRows()
                                        : 0
                        );

                        importJob.setErrorMessage(
                                exception.getMessage()
                        );

                        quizImportJobRepository.save(
                                importJob
                        );

                        log.error(
                                "Quiz import failed. jobCode={}, reactionCode={}",
                                jobCode,
                                reactionCode,
                                exception
                        );

                        throw exception;
                    }
                }, transactionManager)
                .build();
    }

    private void validateAllRows(
            List<QuizCsvRow> rows
    ) {
        Set<Integer> questionOrders =
                new HashSet<>();

        String expectedQuizTitle = null;

        for (QuizCsvRow row : rows) {
            validateQuizCsvRow(row);

            if (!questionOrders.add(
                    row.getQuestionOrder()
            )) {
                throw new IllegalStateException(
                        "Duplicate question_order: "
                                + row.getQuestionOrder()
                );
            }

            String currentTitle =
                    row.getQuizTitle().trim();

            if (expectedQuizTitle == null) {
                expectedQuizTitle =
                        currentTitle;
            } else if (!expectedQuizTitle
                    .equals(currentTitle)) {
                throw new IllegalStateException(
                        "All CSV rows must have the same quiz_title"
                );
            }
        }

        Set<Integer> expectedOrders =
                Set.of(1, 2, 3, 4, 5);

        if (!questionOrders.equals(
                expectedOrders
        )) {
            throw new IllegalStateException(
                    "question_order must contain exactly 1, 2, 3, 4 and 5"
            );
        }
    }

    private void validateQuizCsvRow(
            QuizCsvRow row
    ) {
        if (row == null) {
            throw new IllegalStateException(
                    "CSV row must not be null"
            );
        }

        if (isBlank(row.getQuizTitle())) {
            throw new IllegalStateException(
                    "quiz_title is required"
            );
        }

        if (row.getQuestionOrder() == null) {
            throw new IllegalStateException(
                    "question_order is required"
            );
        }

        if (row.getQuestionOrder() < 1
                || row.getQuestionOrder() > 5) {
            throw new IllegalStateException(
                    "question_order must be from 1 to 5"
            );
        }

        if (isBlank(row.getQuestionText())) {
            throw new IllegalStateException(
                    "question_text is required at question "
                            + row.getQuestionOrder()
            );
        }

        if (isBlank(row.getOptionA())
                || isBlank(row.getOptionB())
                || isBlank(row.getOptionC())
                || isBlank(row.getOptionD())) {
            throw new IllegalStateException(
                    "option_a, option_b, option_c and option_d "
                            + "are required at question "
                            + row.getQuestionOrder()
            );
        }

        validateDuplicateOptions(row);

        if (isBlank(row.getCorrectAnswer())) {
            throw new IllegalStateException(
                    "correct_answer is required at question "
                            + row.getQuestionOrder()
            );
        }

        String correctAnswer =
                row.getCorrectAnswer()
                        .trim()
                        .toUpperCase();

        if (!Set.of("A", "B", "C", "D")
                .contains(correctAnswer)) {
            throw new IllegalStateException(
                    "correct_answer must be A, B, C or D "
                            + "at question "
                            + row.getQuestionOrder()
            );
        }

        row.setCorrectAnswer(correctAnswer);

        if (isBlank(row.getExplanation())) {
            throw new IllegalStateException(
                    "explanation is required at question "
                            + row.getQuestionOrder()
            );
        }
    }

    private void validateDuplicateOptions(
            QuizCsvRow row
    ) {
        Set<String> options =
                Set.of(
                        normalizeOption(row.getOptionA()),
                        normalizeOption(row.getOptionB()),
                        normalizeOption(row.getOptionC()),
                        normalizeOption(row.getOptionD())
                );

        if (options.size() != 4) {
            throw new IllegalStateException(
                    "Question "
                            + row.getQuestionOrder()
                            + " contains duplicate options"
            );
        }
    }
    private String normalizeOption(
            String option
    ) {
        return option.trim()
                .toLowerCase();
    }

    private QuizQuestion mapToQuestion(
            QuizCsvRow row,
            Quiz quiz
    ) {
        return QuizQuestion.builder()
                .quiz(quiz)
                .questionOrder(
                        row.getQuestionOrder()
                )
                .questionText(
                        row.getQuestionText().trim()
                )
                .correctAnswer(
                        row.getCorrectAnswer()
                                .trim()
                                .toUpperCase()
                )
                .explanation(
                        row.getExplanation().trim()
                )
                .status(
                        QuizQuestionStatus.ACTIVE
                )
                .build();
    }


    private int resolveNextVersion(
            QuizRepository quizRepository,
            ReactionDefinition reaction
    ) {
        return quizRepository
                .findTopByReactionOrderByVersionDesc(
                        reaction
                )
                .map(quiz ->
                        quiz.getVersion() + 1
                )
                .orElse(1);
    }

    private String buildQuizCode(
            String reactionCode,
            int version
    ) {
        return "QUIZ_"
                + reactionCode
                + "_V"
                + version;
    }

    private String getStringJobParameter(
            org.springframework.batch.core.scope.context.ChunkContext
                    chunkContext,
            String parameterName
    ) {
        Object value =
                chunkContext
                        .getStepContext()
                        .getJobParameters()
                        .get(parameterName);

        if (value == null
                || value.toString().isBlank()) {
            throw new IllegalStateException(
                    "Missing job parameter: "
                            + parameterName
            );
        }

        return value.toString();
    }

    private boolean isBlank(
            String value
    ) {
        return value == null
                || value.isBlank();
    }

    private void saveQuestionOptions(
            QuizCsvRow row,
            QuizQuestion question,
            QuizQuestionOptionRepository optionRepository
    ) {
        List<QuizQuestionOption> options =
                List.of(
                        createOption(
                                question,
                                "A",
                                row.getOptionA(),
                                1
                        ),
                        createOption(
                                question,
                                "B",
                                row.getOptionB(),
                                2
                        ),
                        createOption(
                                question,
                                "C",
                                row.getOptionC(),
                                3
                        ),
                        createOption(
                                question,
                                "D",
                                row.getOptionD(),
                                4
                        )
                );

        optionRepository.saveAll(options);
    }

    private QuizQuestionOption createOption(
            QuizQuestion question,
            String optionKey,
            String optionText,
            Integer optionOrder
    ) {
        return QuizQuestionOption.builder()
                .question(question)
                .optionKey(optionKey)
                .optionText(optionText.trim())
                .optionOrder(optionOrder)
                .build();
    }
}