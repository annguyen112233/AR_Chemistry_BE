package com.chemistry.demo.repository;

import com.chemistry.demo.entity.QuizQuestion;
import com.chemistry.demo.entity.QuizQuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizQuestionOptionRepository
        extends JpaRepository<QuizQuestionOption, String> {

    List<QuizQuestionOption>
    findByQuestionOrderByOptionOrderAsc(
            QuizQuestion question
    );

    Optional<QuizQuestionOption>
    findByQuestionAndOptionKeyIgnoreCase(
            QuizQuestion question,
            String optionKey
    );


}