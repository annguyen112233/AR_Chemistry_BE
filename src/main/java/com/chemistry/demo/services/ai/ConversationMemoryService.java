package com.chemistry.demo.services.ai;

import com.chemistry.demo.dto.ai.MemoryMatchResult;
import com.chemistry.demo.entity.ConversationMessage;
import com.chemistry.demo.enums.MessageRole;
import com.chemistry.demo.repository.ConversationMessageRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Semantic Memory Retrieval Service.
 * Tìm kiếm tin nhắn USER cũ có nội dung tương tự dựa trên cosine similarity
 * của embedding vectors. Nếu tìm thấy match >= threshold, trả về câu trả lời
 * ASSISTANT cũ tương ứng mà không cần gọi AI model.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationMemoryService {

    private final EmbeddingService embeddingService;
    private final SimilarityService similarityService;
    private final ConversationMessageRepository messageRepository;
    private final ObjectMapper objectMapper;

    private static final double SIMILARITY_THRESHOLD = 0.90;

    /**
     * Tìm USER message cũ có nội dung tương tự nhất.
     * @param message nội dung tin nhắn mới của user
     * @return Optional chứa MemoryMatchResult nếu tìm thấy match >= threshold
     */
    public Optional<MemoryMatchResult> findSimilarUserMessage(String message) {
        try {
            log.info("Starting memory search for message: '{}'",
                    message.length() > 50 ? message.substring(0, 50) + "..." : message);

            // 1. Generate embedding cho message mới
            List<Double> newEmbedding = embeddingService.embed(message);
            if (newEmbedding.isEmpty()) {
                log.warn("Failed to generate embedding, skipping memory search");
                return Optional.empty();
            }

            // 2. Lấy tất cả USER messages có embedding
            List<ConversationMessage> userMessages =
                    messageRepository.findByRoleAndEmbeddingJsonIsNotNull(MessageRole.USER);

            if (userMessages.isEmpty()) {
                log.info("No existing embeddings found in database");
                return Optional.empty();
            }

            // 3. Tìm message có similarity cao nhất
            ConversationMessage bestMatch = null;
            double bestScore = -1.0;

            for (ConversationMessage msg : userMessages) {
                List<Double> existingEmbedding = deserializeEmbedding(msg.getEmbeddingJson());
                if (existingEmbedding.isEmpty()) continue;

                double score = similarityService.cosineSimilarity(newEmbedding, existingEmbedding);
                if (score > bestScore) {
                    bestScore = score;
                    bestMatch = msg;
                }
            }

            log.info("Best similarity score: {}", String.format("%.4f", bestScore));

            // 4. Kiểm tra threshold
            if (bestMatch == null || bestScore < SIMILARITY_THRESHOLD) {
                log.info("No match above threshold {}, score was {}", SIMILARITY_THRESHOLD, String.format("%.4f", bestScore));
                return Optional.empty();
            }

            // 5. Tìm ASSISTANT message tương ứng (ngay sau USER message trong cùng conversation)
            Optional<ConversationMessage> assistantMessage =
                    messageRepository.findFirstByConversationIdAndRoleAndCreatedAtAfterOrderByCreatedAtAsc(
                            bestMatch.getConversation().getId(),
                            MessageRole.ASSISTANT,
                            bestMatch.getCreatedAt()
                    );

            if (assistantMessage.isEmpty()) {
                log.warn("Found similar USER message but no ASSISTANT reply found, skipping reuse");
                return Optional.empty();
            }

            ConversationMessage assistant = assistantMessage.get();
            log.info("Memory match found! Score: {}, reusing answer from message: {}",
                    String.format("%.4f", bestScore), assistant.getId());

            return Optional.of(MemoryMatchResult.builder()
                    .userMessageId(bestMatch.getId())
                    .assistantMessageId(assistant.getId())
                    .answer(assistant.getContent())
                    .similarityScore(bestScore)
                    .conversationId(bestMatch.getConversation().getId())
                    .build());

        } catch (Exception e) {
            log.error("Memory search failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Serialize embedding vector thành JSON string.
     */
    public String serializeEmbedding(List<Double> embedding) {
        try {
            return objectMapper.writeValueAsString(embedding);
        } catch (Exception e) {
            log.error("Failed to serialize embedding: {}", e.getMessage());
            return null;
        }
    }

    private List<Double> deserializeEmbedding(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Double>>() {});
        } catch (Exception e) {
            log.error("Failed to deserialize embedding: {}", e.getMessage());
            return List.of();
        }
    }
}
