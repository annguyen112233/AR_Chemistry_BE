package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {
    Optional<Lesson> findByLessonCode(String lessonCode);
    boolean existsByLessonCode(String lessonCode);

    Page<Lesson> findAllByOrderByLessonNumberAsc(Pageable pageable);
}
