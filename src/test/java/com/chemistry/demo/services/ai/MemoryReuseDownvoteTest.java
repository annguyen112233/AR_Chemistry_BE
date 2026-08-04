package com.chemistry.demo.services.ai;

import com.chemistry.demo.dto.ai.MemoryMatchResult;
import com.chemistry.demo.entity.Conversation;
import com.chemistry.demo.entity.ConversationMessage;
import com.chemistry.demo.enums.MessageRole;
import com.chemistry.demo.repository.ConversationMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * 👎 phải loại câu trả lời khỏi memory reuse: câu hỏi giống hệt (similarity
 * 100%) nhưng câu trả lời cache đã bị chấm 👎 thì KHÔNG được phục vụ lại —
 * hệ thống phải gọi model mới. Đây là van tự chữa lành cho cache câu sai.
 */
@ExtendWith(MockitoExtension.class)
class MemoryReuseDownvoteTest {

    @Mock private EmbeddingService embeddingService;
    @Mock private ConversationMessageRepository messageRepository;

    private ConversationMemoryService memoryService;

    private ConversationMessage cachedUserMessage;
    private ConversationMessage cachedAssistantReply;

    @BeforeEach
    void setUp() throws Exception {
        ObjectMapper realMapper = new ObjectMapper();
        memoryService = new ConversationMemoryService(
                embeddingService,
                new SimilarityService(),
                messageRepository,
                realMapper);

        List<Double> vector = List.of(0.6, 0.8);
        String vectorJson = realMapper.writeValueAsString(vector);

        Conversation conversation = Conversation.builder().build();
        conversation.setId("conv-1");

        cachedUserMessage = ConversationMessage.builder()
                .id("user-msg-1")
                .conversation(conversation)
                .role(MessageRole.USER)
                .content("Giải thích cấu trúc phân tử nước H₂O")
                .embeddingJson(vectorJson)
                .build();
        cachedUserMessage.setCreatedAt(Instant.now());

        cachedAssistantReply = ConversationMessage.builder()
                .id("ai-msg-1")
                .conversation(conversation)
                .role(MessageRole.ASSISTANT)
                .content("H₂O phân tử nước có cấu trúc circumvent với hai 原子...")
                .build();

        // Câu hỏi mới có embedding TRÙNG KHỚP → similarity = 1.0 (100%).
        lenient().when(embeddingService.embed(anyString())).thenReturn(vector);
        lenient().when(messageRepository.findByRoleAndEmbeddingJsonIsNotNull(MessageRole.USER))
                .thenReturn(List.of(cachedUserMessage));
        lenient().when(messageRepository
                .findFirstByConversationIdAndRoleAndCreatedAtAfterOrderByCreatedAtAsc(
                        anyString(), any(), any()))
                .thenReturn(Optional.of(cachedAssistantReply));
    }

    @Test
    @DisplayName("Câu trả lời chưa chấm: reuse bình thường")
    void unratedAnswerIsReused() {
        Optional<MemoryMatchResult> match =
                memoryService.findSimilarUserMessage("Giải thích cấu trúc phân tử nước H₂O");

        assertThat(match).isPresent();
        assertThat(match.get().getAssistantMessageId()).isEqualTo("ai-msg-1");
        assertThat(match.get().getSimilarityScore()).isGreaterThan(0.99);
    }

    @Test
    @DisplayName("Câu trả lời bị 👎: KHÔNG reuse dù similarity 100%")
    void downvotedAnswerIsExcluded() {
        cachedAssistantReply.setRating(-1);

        Optional<MemoryMatchResult> match =
                memoryService.findSimilarUserMessage("Giải thích cấu trúc phân tử nước H₂O");

        assertThat(match).isEmpty();
    }

    @Test
    @DisplayName("Câu trả lời được 👍: vẫn reuse bình thường")
    void upvotedAnswerIsReused() {
        cachedAssistantReply.setRating(1);

        Optional<MemoryMatchResult> match =
                memoryService.findSimilarUserMessage("Giải thích cấu trúc phân tử nước H₂O");

        assertThat(match).isPresent();
    }
}
