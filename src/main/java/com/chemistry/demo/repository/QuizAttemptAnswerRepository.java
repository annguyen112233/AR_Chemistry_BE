package com.chemistry.demo.repository;

import com.chemistry.demo.entity.QuizAttempt;
import com.chemistry.demo.entity.QuizAttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface QuizAttemptAnswerRepository extends JpaRepository<QuizAttemptAnswer, UUID> {

    @Query("""
       SELECT a
       FROM QuizAttemptAnswer a
       JOIN a.question q
       WHERE a.attempt = :attempt
       ORDER BY q.questionOrder ASC
       """)
    List<QuizAttemptAnswer> findByAttemptOrderByQuestionOrderAsc(@Param("attempt") QuizAttempt attempt);

}