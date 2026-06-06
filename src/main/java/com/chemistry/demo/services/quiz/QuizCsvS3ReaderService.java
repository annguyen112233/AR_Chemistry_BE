package com.chemistry.demo.services.quiz;

import com.chemistry.demo.dto.quizCSV.QuizCsvRow;

import java.util.List;

public interface QuizCsvS3ReaderService {
    List<QuizCsvRow> readCsvFromS3(String s3Key);
}
