package com.chemistry.demo.config.seeder;

import com.chemistry.demo.entity.Quiz;
import com.chemistry.demo.entity.QuizQuestion;
import com.chemistry.demo.entity.QuizQuestionOption;
import com.chemistry.demo.entity.ReactionDefinition;
import com.chemistry.demo.enums.QuizQuestionStatus;
import com.chemistry.demo.enums.QuizStatus;
import com.chemistry.demo.repository.QuizQuestionOptionRepository;
import com.chemistry.demo.repository.QuizQuestionRepository;
import com.chemistry.demo.repository.QuizRepository;
import com.chemistry.demo.repository.ReactionDefinitionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuizSeeder implements DataSeeder {

    private static final String QUIZ_JSON_PATH = "seed/quizzes.json";

    private final ObjectMapper objectMapper;
    private final ReactionDefinitionRepository reactionDefinitionRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizQuestionOptionRepository quizQuestionOptionRepository;

    @Override
    @Transactional
    public void seed() {
        try (InputStream inputStream =
                     new ClassPathResource(QUIZ_JSON_PATH).getInputStream()) {

            List<QuizSeedData> quizSeedData =
                    objectMapper.readValue(
                            inputStream,
                            new TypeReference<List<QuizSeedData>>() {}
                    );

            for (QuizSeedData data : quizSeedData) {
                seedQuiz(data);
            }

            log.info("Quiz seeding completed. Total definitions: {}", quizSeedData.size());

        } catch (Exception exception) {
            log.error("Failed to seed quizzes", exception);
            throw new IllegalStateException("Failed to seed quizzes", exception);
        }
    }

    private void seedQuiz(QuizSeedData data) {
        if (quizRepository.findByQuizCode(data.getQuizCode()).isPresent()) {
            log.info("Quiz {} already exists, skipping.", data.getQuizCode());
            return;
        }

        ReactionDefinition reaction =
                reactionDefinitionRepository.findByCode(data.getReactionCode())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Reaction not found for quiz "
                                                + data.getQuizCode()
                                                + ": "
                                                + data.getReactionCode()
                                )
                        );

        validateQuizData(data);

        Quiz quiz = Quiz.builder()
                .quizCode(data.getQuizCode())
                .reaction(reaction)
                .title(data.getTitle())
                .generatedBy(data.getGeneratedBy())
                .status(QuizStatus.valueOf(data.getStatus()))
                .version(data.getVersion())
                .questionLimit(data.getQuestionLimit())
                .durationSeconds(data.getDurationSeconds())
                .build();

        quizRepository.save(quiz);

        for (QuestionSeedData questionData : data.getQuestions()) {
            QuizQuestion question = QuizQuestion.builder()
                    .quiz(quiz)
                    .questionText(questionData.getQuestionText())
                    .questionOrder(questionData.getQuestionOrder())
                    .correctAnswer(normalizeOptionKey(questionData.getCorrectAnswer()))
                    .explanation(questionData.getExplanation())
                    .status(QuizQuestionStatus.ACTIVE)
                    .build();

            quizQuestionRepository.save(question);

            for (OptionSeedData optionData : questionData.getOptions()) {
                QuizQuestionOption option = QuizQuestionOption.builder()
                        .question(question)
                        .optionKey(normalizeOptionKey(optionData.getOptionKey()))
                        .optionText(optionData.getOptionText())
                        .optionOrder(optionData.getOptionOrder())
                        .build();

                quizQuestionOptionRepository.save(option);
            }
        }

        log.info(
                "Seeded quiz {} for reaction {} with {} questions.",
                quiz.getQuizCode(),
                reaction.getCode(),
                data.getQuestions().size()
        );
    }

    private void validateQuizData(QuizSeedData data) {
        if (data.getQuestions() == null
                || data.getQuestions().size() != data.getQuestionLimit()) {
            throw new IllegalStateException(
                    "Quiz "
                            + data.getQuizCode()
                            + " must contain exactly "
                            + data.getQuestionLimit()
                            + " questions."
            );
        }

        for (QuestionSeedData question : data.getQuestions()) {
            if (question.getOptions() == null || question.getOptions().size() != 4) {
                throw new IllegalStateException(
                        "Question "
                                + question.getQuestionOrder()
                                + " in quiz "
                                + data.getQuizCode()
                                + " must contain exactly 4 options."
                );
            }

            boolean correctOptionExists =
                    question.getOptions()
                            .stream()
                            .anyMatch(option ->
                                    normalizeOptionKey(option.getOptionKey())
                                            .equals(
                                                    normalizeOptionKey(
                                                            question.getCorrectAnswer()
                                                    )
                                            )
                            );

            if (!correctOptionExists) {
                throw new IllegalStateException(
                        "Correct answer "
                                + question.getCorrectAnswer()
                                + " does not exist in quiz "
                                + data.getQuizCode()
                                + ", question "
                                + question.getQuestionOrder()
                );
            }
        }
    }

    private String normalizeOptionKey(String value) {
        if (value == null) {
            return "";
        }

        return value.trim().toUpperCase();
    }

    @Override
    public int getOrder() {
        /*
         * Phải chạy sau:
         * - chemical card seeder
         * - reaction seeder
         *
         * AccountSeeder của bạn đang là order 3.
         * Có thể đổi QuizSeeder thành 5 hoặc số lớn hơn ReactionSeeder.
         */
        return 13;
    }

    @Data
    public static class QuizSeedData {
        private String quizCode;
        private String reactionCode;
        private String title;
        private String generatedBy;
        private String status;
        private Integer version;
        private Integer questionLimit;
        private Integer durationSeconds;
        private List<QuestionSeedData> questions;
    }

    @Data
    public static class QuestionSeedData {
        private Integer questionOrder;
        private String questionText;
        private String correctAnswer;
        private String explanation;
        private List<OptionSeedData> options;
    }

    @Data
    public static class OptionSeedData {
        private String optionKey;
        private String optionText;
        private Integer optionOrder;
    }
}