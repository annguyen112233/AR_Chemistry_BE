package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Feedback;
import com.chemistry.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String> {
    Page<Feedback> findByUser(User user, Pageable pageable);
    Optional<Feedback> findByIdAndUser(String id, User user);

}
