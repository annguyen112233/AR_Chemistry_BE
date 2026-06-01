package com.chemistry.demo.repository;

import com.chemistry.demo.entity.QuizImportJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizImportJobRepository extends JpaRepository<QuizImportJob, String> {
    Optional<QuizImportJob> findByJobCode(String jobCode);
}
