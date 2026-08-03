package com.chemistry.demo.repository;

import com.chemistry.demo.entity.QuizImportJob;
import com.chemistry.demo.entity.ReactionDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizImportJobRepository
        extends JpaRepository<QuizImportJob, String> {

    Optional<QuizImportJob> findByJobCode(
            String jobCode
    );

    Page<QuizImportJob>
    findByReactionOrderByCreatedAtDesc(
            ReactionDefinition reaction,
            Pageable pageable
    );
}
