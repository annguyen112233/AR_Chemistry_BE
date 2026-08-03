package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Quiz;
import com.chemistry.demo.entity.QuizAttempt;
import com.chemistry.demo.entity.ReactionDefinition;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.QuizAttemptStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface QuizAttemptRepository
        extends JpaRepository<QuizAttempt, String> {

    Optional<QuizAttempt> findByAttemptCode(
            String attemptCode
    );

    Optional<QuizAttempt> findByAttemptCodeAndStudent(
            String attemptCode,
            User student
    );

    Page<QuizAttempt> findByStudentOrderByCreatedAtDesc(
            User student,
            Pageable pageable
    );

    Page<QuizAttempt> findByStudentAndQuizOrderByCreatedAtDesc(
            User student,
            Quiz quiz,
            Pageable pageable
    );

    Page<QuizAttempt> findAllByOrderByCreatedAtDesc(
            Pageable pageable
    );

    Page<QuizAttempt>
    findByStudentAndStatusInOrderByCreatedAtDesc(
            User student,
            Collection<QuizAttemptStatus> statuses,
            Pageable pageable
    );

    Optional<QuizAttempt>
    findFirstByStudentAndQuizAndStatusInOrderByCreatedAtDesc(
            User student,
            Quiz quiz,
            Collection<QuizAttemptStatus> statuses
    );

    Optional<QuizAttempt>
    findFirstByStudentAndQuizReactionAndStatusInOrderByCreatedAtDesc(
            User student,
            ReactionDefinition reaction,
            Collection<QuizAttemptStatus> statuses
    );

    Page<QuizAttempt>
    findByStudentAndQuizReactionAndStatusInOrderByCreatedAtDesc(
            User student,
            ReactionDefinition reaction,
            Collection<QuizAttemptStatus> statuses,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT attempt.quiz.reaction.id
        FROM QuizAttempt attempt
        WHERE attempt.student = :student
          AND attempt.status IN :statuses
    """)
    Set<String> findCompletedReactionIds(
            @Param("student") User student,
            @Param("statuses")
            Collection<QuizAttemptStatus> statuses
    );

    List<QuizAttempt>
    findTop100ByStatusAndExpiredAtBeforeOrderByExpiredAtAsc(
            QuizAttemptStatus status,
            Instant expiredAt
    );
}