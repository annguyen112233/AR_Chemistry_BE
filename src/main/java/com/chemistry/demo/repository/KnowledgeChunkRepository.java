package com.chemistry.demo.repository;

import com.chemistry.demo.entity.KnowledgeChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeChunkRepository extends JpaRepository<KnowledgeChunk, String> {

    /**
     * Lấy toàn bộ chunk đã có embedding để quét cosine similarity trong bộ nhớ.
     */
    List<KnowledgeChunk> findByEmbeddingJsonIsNotNull();

    long countByEmbeddingJsonIsNotNull();

    @Modifying
    @Query("delete from KnowledgeChunk k where k.sourceType = :sourceType")
    void deleteBySourceType(String sourceType);
}
