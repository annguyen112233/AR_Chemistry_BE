package com.chemistry.demo.dto.quizCSV;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizCsvRow {

    @CsvBindByName(
            column = "quiz_title",
            required = true
    )
    private String quizTitle;

    @CsvBindByName(
            column = "question_order",
            required = true
    )
    private Integer questionOrder;

    @CsvBindByName(
            column = "question_text",
            required = true
    )
    private String questionText;

    @CsvBindByName(
            column = "option_a",
            required = true
    )
    private String optionA;

    @CsvBindByName(
            column = "option_b",
            required = true
    )
    private String optionB;

    @CsvBindByName(
            column = "option_c",
            required = true
    )
    private String optionC;

    @CsvBindByName(
            column = "option_d",
            required = true
    )
    private String optionD;

    @CsvBindByName(
            column = "correct_answer",
            required = true
    )
    private String correctAnswer;

    @CsvBindByName(
            column = "explanation",
            required = true
    )
    private String explanation;
}