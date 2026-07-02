package com.chemistry.demo.repository;

import com.chemistry.demo.entity.NotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTokenRepository
        extends JpaRepository<NotificationToken, String> {

    Optional<NotificationToken> findByFcmToken(String fcmToken);

    List<NotificationToken> findByUserIdAndActiveTrue(String userId);

    Optional<NotificationToken> findByUserIdAndFcmToken(String userId, String fcmToken);
    List<NotificationToken> findByActiveTrue();
}