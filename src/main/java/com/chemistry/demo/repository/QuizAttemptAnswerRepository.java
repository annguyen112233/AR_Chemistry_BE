package com.chemistry.demo.repository;

import com.chemistry.demo.entity.QuizAttempt;
import com.chemistry.demo.entity.QuizAttemptAnswer;
import com.chemistry.demo.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptAnswerRepository
        extends JpaRepository<QuizAttemptAnswer, String> {

    @Query("""
        SELECT answer
        FROM QuizAttemptAnswer answer
        JOIN FETCH answer.question question
        WHERE answer.attempt = :attempt
        ORDER BY question.questionOrder ASC
    """)
    List<QuizAttemptAnswer>
    findByAttemptOrderByQuestionOrderAsc(
            @Param("attempt") QuizAttempt attempt
    );

    Optional<QuizAttemptAnswer> findByAttemptAndQuestion(
            QuizAttempt attempt,
            QuizQuestion question
    );

    long countByAttempt(QuizAttempt attempt);
}