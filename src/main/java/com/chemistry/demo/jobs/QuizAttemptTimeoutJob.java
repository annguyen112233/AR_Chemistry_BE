package com.chemistry.demo.jobs;

import com.chemistry.demo.entity.QuizAttempt;
import com.chemistry.demo.enums.QuizAttemptStatus;
import com.chemistry.demo.repository.QuizAttemptRepository;
import com.chemistry.demo.services.quiz.impl.StudentQuizServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuizAttemptTimeoutJob {

    private final QuizAttemptRepository
            quizAttemptRepository;

    private final StudentQuizServiceImpl
            studentQuizService;

    @Scheduled(fixedDelay = 10_000)
    @Transactional
    public void finalizeExpiredAttempts() {
        List<QuizAttempt> attempts =
                quizAttemptRepository
                        .findTop100ByStatusAndExpiredAtBeforeOrderByExpiredAtAsc(
                                QuizAttemptStatus.RUNNING,
                                Instant.now()
                        );

        for (QuizAttempt attempt : attempts) {
            try {
                studentQuizService.finalizeAttempt(
                        attempt,
                        QuizAttemptStatus.TIMEOUT
                );
            } catch (Exception exception) {
                log.error(
                        "Cannot timeout quiz attempt {}",
                        attempt.getAttemptCode(),
                        exception
                );
            }
        }
    }
}