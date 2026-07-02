package com.chemistry.demo.repository;

import com.chemistry.demo.entity.KnowledgePointWallet;
import com.chemistry.demo.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KnowledgePointWalletRepository
        extends JpaRepository<KnowledgePointWallet, String> {

    Optional<KnowledgePointWallet> findByUserId(String userId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from KnowledgePointWallet w where w.userId = :userId")
    Optional<KnowledgePointWallet> findByUserIdForUpdate(@Param("userId") String userId);
}