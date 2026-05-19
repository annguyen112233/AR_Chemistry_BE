package com.chemistry.demo.repository;

import com.chemistry.demo.entity.ConversationMessage;
import com.chemistry.demo.enums.MessageRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, String> {
    List<ConversationMessage> findByConversationIdOrderByCreatedAtAsc(String conversationId);

    List<ConversationMessage> findByRoleAndEmbeddingJsonIsNotNull(MessageRole role);

    Optional<ConversationMessage> findFirstByConversationIdAndRoleAndCreatedAtAfterOrderByCreatedAtAsc(
            String conversationId, MessageRole role, Instant createdAt);
}
