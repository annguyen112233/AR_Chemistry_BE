package com.chemistry.demo.repository;

import com.chemistry.demo.entity.KnowledgePointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgePointTransactionRepository extends JpaRepository<KnowledgePointTransaction, String> {
    List<KnowledgePointTransaction> findTop20ByUserIdOrderByCreatedAtDesc(String userId);
}
