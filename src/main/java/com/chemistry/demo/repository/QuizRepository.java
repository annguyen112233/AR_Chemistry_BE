package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Quiz;
import com.chemistry.demo.entity.ReactionDefinition;
import com.chemistry.demo.enums.QuizStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository
        extends JpaRepository<Quiz, String> {

    Optional<Quiz> findByQuizCode(String quizCode);

    Optional<Quiz> findByQuizCodeAndStatus(
            String quizCode,
            QuizStatus status
    );

    Optional<Quiz> findTopByReactionOrderByVersionDesc(
            ReactionDefinition reaction
    );

    List<Quiz> findByReactionAndStatus(
            ReactionDefinition reaction,
            QuizStatus status
    );

    Optional<Quiz> findTopByReactionOrderByCreatedAtDesc(
            ReactionDefinition reaction
    );

    Page<Quiz> findByReactionOrderByCreatedAtDesc(
            ReactionDefinition reaction,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "reaction")
    Optional<Quiz>
    findTopByReactionAndStatusOrderByCreatedAtDesc(
            ReactionDefinition reaction,
            QuizStatus status
    );

    @EntityGraph(attributePaths = "reaction")
    List<Quiz> findByStatusOrderByCreatedAtDesc(
            QuizStatus status
    );

    boolean existsByReactionAndStatus(
            ReactionDefinition reaction,
            QuizStatus status
    );


}