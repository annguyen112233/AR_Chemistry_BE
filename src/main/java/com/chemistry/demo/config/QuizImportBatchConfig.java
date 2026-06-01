package com.chemistry.demo.config;

import com.chemistry.demo.dto.quiz.QuizCsvRow;
import com.chemistry.demo.entity.Lesson;
import com.chemistry.demo.entity.Quiz;
import com.chemistry.demo.entity.QuizImportJob;
import com.chemistry.demo.entity.QuizQuestion;
import com.chemistry.demo.repository.LessonRepository;
import com.chemistry.demo.repository.QuizImportJobRepository;
import com.chemistry.demo.repository.QuizQuestionRepository;
import com.chemistry.demo.repository.QuizRepository;
import com.chemistry.demo.services.quiz.QuizCsvS3ReaderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class QuizImportBatchConfig {

    @Bean
    public Job importQuizJob(JobRepository jobRepository, Step importQuizStep) {
        return new JobBuilder("importQuizJob", jobRepository)
                .start(importQuizStep)
                .build();
    }

    @Bean
    public Step importQuizStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            QuizCsvS3ReaderService quizCsvS3ReaderService,
            QuizImportJobRepository quizImportJobRepository,
            LessonRepository lessonRepository,
            QuizRepository quizRepository,
            QuizQuestionRepository quizQuestionRepository,
            ObjectMapper objectMapper
    ) {
        return new StepBuilder("importQuizStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String jobCode = (String) chunkContext.getStepContext().getJobParameters().get("jobCode");
                    String lessonCode = (String) chunkContext.getStepContext().getJobParameters().get("lessonCode");
                    String s3Key = (String) chunkContext.getStepContext().getJobParameters().get("s3Key");

                    QuizImportJob importJob = quizImportJobRepository.findByJobCode(jobCode)
                            .orElseThrow(() -> new RuntimeException("Import job not found: " + jobCode));

                    try {
                        importJob.setStatus("PROCESSING");
                        quizImportJobRepository.save(importJob);

                        Lesson lesson = lessonRepository.findByLessonCode(lessonCode)
                                .orElseThrow(() -> new RuntimeException("Lesson not found: " + lessonCode));

                        List<QuizCsvRow> rows = quizCsvS3ReaderService.readCsvFromS3(s3Key);

                        if (rows.isEmpty()) {
                            throw new RuntimeException("CSV has no rows");
                        }

                        QuizCsvRow first = rows.get(0);

                        Quiz quiz = new Quiz();
                        quiz.setLesson(lesson);
                        quiz.setQuizCode("quiz_" + lesson.getLessonCode() + "_v" + System.currentTimeMillis());
                        quiz.setTitle(first.getQuizTitle());
                        quiz.setGeneratedBy("external_ai_csv");
                        quiz.setStatus("draft");
                        quiz.setVersion(1);
                        quizRepository.save(quiz);

                        int success = 0;
                        int failed = 0;

                        for (QuizCsvRow row : rows) {
                            try {


                                validateQuizCsvRow(row, lessonCode);

                                QuizQuestion question = new QuizQuestion();
                                question.setQuiz(quiz);
                                question.setQuestionOrder(row.getQuestionOrder());
                                question.setType(row.getType());
                                question.setQuestionText(row.getQuestionText());
                                question.setOptionsJson(objectMapper.writeValueAsString(buildOptions(row)));
                                question.setCorrectAnswer(row.getCorrectAnswer());
                                question.setExplanation(row.getExplanation());
                                question.setDifficulty(row.getDifficulty());
                                question.setStatus("active");

                                quizQuestionRepository.save(question);

                                System.out.println("Saved question order = " + row.getQuestionOrder());
                                success++;

                            } catch (Exception ex) {
                                failed++;
                            }
                        }

                        importJob.setQuiz(quiz);
                        importJob.setTotalRows(rows.size());
                        importJob.setSuccessRows(success);
                        importJob.setFailedRows(failed);
                        importJob.setStatus(failed == 0 ? "COMPLETED" : "PARTIAL_FAILED");
                        quizImportJobRepository.save(importJob);

                        return RepeatStatus.FINISHED;

                    } catch (Exception e) {
                        importJob.setStatus("FAILED");
                        importJob.setErrorMessage(e.getMessage());
                        quizImportJobRepository.save(importJob);
                        throw e;
                    }
                }, transactionManager)
                .build();
    }

    private void validateQuizCsvRow(QuizCsvRow row, String lessonCode) {
        if (row.getLessonCode() == null || !row.getLessonCode().equals(lessonCode)) {
            throw new RuntimeException("Invalid lesson_code");
        }

        if (row.getQuizTitle() == null || row.getQuizTitle().isBlank()) {
            throw new RuntimeException("quiz_title is required");
        }

        if (row.getQuestionOrder() == null) {
            throw new RuntimeException("question_order is required");
        }

        if (row.getType() == null || row.getType().isBlank()) {
            throw new RuntimeException("type is required");
        }

        if (!List.of("multiple_choice", "true_false", "fill_blank").contains(row.getType())) {
            throw new RuntimeException("Invalid question type: " + row.getType());
        }

        if (row.getQuestionText() == null || row.getQuestionText().isBlank()) {
            throw new RuntimeException("question_text is required");
        }

        if (row.getCorrectAnswer() == null || row.getCorrectAnswer().isBlank()) {
            throw new RuntimeException("correct_answer is required");
        }

        if ("multiple_choice".equals(row.getType())) {
            if (isBlank(row.getOptionA()) || isBlank(row.getOptionB())
                    || isBlank(row.getOptionC()) || isBlank(row.getOptionD())) {
                throw new RuntimeException("multiple_choice requires option_a to option_d");
            }
        }

        if ("true_false".equals(row.getType())) {
            String ans = row.getCorrectAnswer().trim().toUpperCase();
            if (!ans.equals("TRUE") && !ans.equals("FALSE")) {
                throw new RuntimeException("true_false correct_answer must be TRUE or FALSE");
            }
        }

        if ("fill_blank".equals(row.getType())) {
            if (!row.getQuestionText().contains("____")) {
                throw new RuntimeException("fill_blank question_text must contain ____");
            }
        }
    }

    private List<String> buildOptions(QuizCsvRow row) {
        if (!"multiple_choice".equals(row.getType())) {
            return List.of();
        }

        return List.of(
                row.getOptionA(),
                row.getOptionB(),
                row.getOptionC(),
                row.getOptionD()
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}