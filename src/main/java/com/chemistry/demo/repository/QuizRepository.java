package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Lesson;
import com.chemistry.demo.entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, String> {
    List<Quiz> findByLessonAndStatus(Lesson lesson, String status);
    Optional<Quiz> findTopByLessonOrderByCreatedAtDesc(Lesson lesson);

    Page<Quiz> findByLessonOrderByCreatedAtDesc(Lesson lesson,
                                                Pageable pageable);

    Optional<Quiz> findByQuizCode(String quizCode);

    Optional<Quiz> findTopByLessonAndStatusOrderByCreatedAtDesc(Lesson lesson, String status);

    Optional<Quiz> findByQuizCodeAndStatus(String quizCode, String status);

    List<Quiz> findByStatusOrderByCreatedAtDesc(String status);
}
