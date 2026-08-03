package com.chemistry.demo.dto.quizCSV;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByNames;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizCsvRow {

    @CsvBindByNames({
            @CsvBindByName(column = "lesson_code"),
            @CsvBindByName(column = "\uFEFFlesson_code")
    })
    private String lessonCode;

    @CsvBindByName(column = "quiz_title")
    private String quizTitle;

    @CsvBindByName(column = "question_order")
    private Integer questionOrder;

    @CsvBindByName(column = "type")
    private String type;

    @CsvBindByName(column = "question_text")
    private String questionText;

    @CsvBindByName(column = "option_a")
    private String optionA;

    @CsvBindByName(column = "option_b")
    private String optionB;

    @CsvBindByName(column = "option_c")
    private String optionC;

    @CsvBindByName(column = "option_d")
    private String optionD;

    @CsvBindByName(column = "correct_answer")
    private String correctAnswer;

    @CsvBindByName(column = "explanation")
    private String explanation;

    @CsvBindByName(column = "difficulty")
    private String difficulty;
}