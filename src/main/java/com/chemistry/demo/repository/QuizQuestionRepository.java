package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Quiz;
import com.chemistry.demo.entity.QuizQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, String> {
    Page<QuizQuestion> findByQuizOrderByQuestionOrderAsc(Quiz quiz,
                                                         Pageable pageable);
    long countByQuiz(Quiz quiz);

    List<QuizQuestion> findByQuizAndStatusOrderByQuestionOrderAsc(Quiz quiz, String status);


}
