package com.chemistry.demo.dto.response.quiz.student;

import com.chemistry.demo.enums.QuizAttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteArResponse {

    private String attemptCode;

    private QuizAttemptStatus status;

    private Instant startedAt;

    private Instant expiredAt;

    private Long remainingSeconds;
}