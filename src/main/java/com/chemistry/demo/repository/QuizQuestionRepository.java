package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Quiz;
import com.chemistry.demo.entity.QuizQuestion;
import com.chemistry.demo.enums.QuizQuestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizQuestionRepository
        extends JpaRepository<QuizQuestion, String> {

    List<QuizQuestion>
    findByQuizAndStatusOrderByQuestionOrderAsc(
            Quiz quiz,
            QuizQuestionStatus status
    );

    long countByQuiz(Quiz quiz);

    long countByQuizAndStatus(
            Quiz quiz,
            QuizQuestionStatus status
    );

    Optional<QuizQuestion> findByIdAndQuiz(
            String questionId,
            Quiz quiz
    );

    boolean existsByQuizAndQuestionOrder(
            Quiz quiz,
            Integer questionOrder
    );

    Page<QuizQuestion>
    findByQuizOrderByQuestionOrderAsc(
            Quiz quiz,
            Pageable pageable
    );

    List<QuizQuestion>
    findByQuizOrderByQuestionOrderAsc(
            Quiz quiz
    );


}