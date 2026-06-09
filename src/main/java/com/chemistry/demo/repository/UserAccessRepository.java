package com.chemistry.demo.repository;

import com.chemistry.demo.entity.User;
import com.chemistry.demo.entity.UserAccess;
import com.chemistry.demo.enums.AccessSource;
import com.chemistry.demo.enums.AccessStatus;
import com.chemistry.demo.enums.AccessType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccessRepository extends JpaRepository<UserAccess, String> {

    Optional<UserAccess> findFirstByUserAndStatusAndExpiredAtAfterOrderByExpiredAtDesc(
            User user,
            AccessStatus status,
            Instant now
    );

    List<UserAccess> findByStatusAndExpiredAtBefore(
            AccessStatus status,
            Instant now
    );

    Optional<UserAccess> findByUserAndAccessTypeAndSourceAndReferenceId(
            User user,
            AccessType accessType,
            AccessSource source,
            String referenceId
    );



    List<UserAccess> findByUserAndStatusAndExpiredAtAfter(User user, AccessStatus accessStatus, Instant now);
}
