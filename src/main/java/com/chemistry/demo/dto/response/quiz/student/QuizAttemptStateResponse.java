package com.chemistry.demo.dto.response.quiz.student;

import com.chemistry.demo.enums.QuizAttemptStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class QuizAttemptStateResponse {

    private String attemptCode;

    private String quizCode;

    private String reactionId;

    private QuizAttemptStatus status;

    private Boolean arCompleted;

    private Boolean quizUnlocked;

    private Instant startedAt;

    private Instant expiredAt;

    private Long remainingSeconds;

    private Boolean submitted;
}