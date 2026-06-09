package com.chemistry.demo.repository;

import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.RoleName;
import com.chemistry.demo.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @EntityGraph(attributePaths = {
            "roles",
            "roles.permissions"
    })
    Optional<User> findByCognitoSub(String cognitoSub);

    @EntityGraph(attributePaths = {
            "roles",
    })
    @Query("SELECT u FROM User u")
    Page<User> findAllWithRoles(Pageable pageable);

    boolean existsUserByEmail(String email);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {
            "roles",
            "roles.permissions"
    })
    Optional<User> findByEmail(String email);

    @Query("""
            SELECT COUNT(u)
            FROM User u
            JOIN u.roles r
            WHERE r.roleName = :roleName
              AND u.status = :status
            """)
    long countByRoleNameAndStatus(
            @Param("roleName") RoleName roleName,
            @Param("status") UserStatus status
    );
}
