package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Subscriptions;
import com.chemistry.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscriptions, String> {
    boolean existsByUserAndActiveTrue(User user);

    @Modifying
    @Query("""
                UPDATE Subscriptions s
                SET s.active = false
                WHERE s.user = :user
            """)
    void deactivateAllByUser(
            @Param("user") User user
    );

    Optional<Subscriptions> findByUserAndActiveTrue(User user);
}
