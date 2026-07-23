package com.chemistry.demo.dto.response.quiz.student;

import com.chemistry.demo.enums.QuizAttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartQuizResponse {

    private String attemptCode;

    private String quizCode;

    private QuizAttemptStatus status;

}