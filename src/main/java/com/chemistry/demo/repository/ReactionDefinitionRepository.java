package com.chemistry.demo.repository;

import com.chemistry.demo.entity.ReactionDefinition;
import com.chemistry.demo.enums.ArSceneKey;
import com.chemistry.demo.enums.ReactionCategory;
import com.chemistry.demo.enums.ReactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionDefinitionRepository
        extends JpaRepository<ReactionDefinition, String> {

    Optional<ReactionDefinition> findByCode(String code);

    Optional<ReactionDefinition> findByReactantKey(String reactantKey);

    boolean existsByCode(String code);

    Page<ReactionDefinition> findByActiveTrue(Pageable pageable);

    Page<ReactionDefinition> findByReactionTypeAndActiveTrue(
            ReactionType reactionType,
            Pageable pageable
    );

    Page<ReactionDefinition> findByArSceneKeyAndActiveTrue(
            ArSceneKey arSceneKey,
            Pageable pageable
    );

    Page<ReactionDefinition>
    findByGradeAndReactionCategoryAndNameContainingIgnoreCaseAndActiveTrue(
            Integer grade,
            ReactionCategory reactionCategory,
            String keyword,
            Pageable pageable
    );

    long countByActiveTrue();
    Page<ReactionDefinition> findByReactionCategory(
            ReactionCategory reactionCategory,
            Pageable pageable
    );

    Page<ReactionDefinition> findByGrade(
            Integer grade,
            Pageable pageable
    );

    Page<ReactionDefinition>
    findByGradeAndReactionCategory(
            Integer grade,
            ReactionCategory reactionCategory,
            Pageable pageable
    );

}
