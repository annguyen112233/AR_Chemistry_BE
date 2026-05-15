package com.chemistry.demo.repository;

import com.chemistry.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

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
}
