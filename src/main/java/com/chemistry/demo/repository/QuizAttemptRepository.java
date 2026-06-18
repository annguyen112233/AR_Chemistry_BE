package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Quiz;
import com.chemistry.demo.entity.QuizAttempt;
import com.chemistry.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {

    Optional<QuizAttempt> findByAttemptCode(String attemptCode);
    Page<QuizAttempt> findByStudentOrderByCreatedAtDesc(User student, Pageable pageable);
    Page<QuizAttempt> findByStudentAndQuizOrderByCreatedAtDesc(
            User student,
            Quiz quiz,
            Pageable pageable
    );
    Optional<QuizAttempt> findByAttemptCodeAndStudent(String attemptCode, User student);
    Page<QuizAttempt> findAllByOrderByCreatedAtDesc(Pageable pageable);

}
