package com.chemistry.demo.services.ai;

import com.chemistry.demo.entity.Conversation;
import com.chemistry.demo.entity.ConversationMessage;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.MessageRole;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.mapper.ConversationMapper;
import com.chemistry.demo.repository.ConversationMessageRepository;
import com.chemistry.demo.repository.ConversationRepository;
import com.chemistry.demo.services.ai.Impl.AiChatServiceImpl;
import com.chemistry.demo.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test luồng đánh giá 👍/👎 câu trả lời AI:
 * - validate giá trị rating và quyền sở hữu
 * - 👎 phải loại câu trả lời khỏi memory reuse (tự chữa lành cache sai)
 */
@ExtendWith(MockitoExtension.class)
class MessageRatingTest {

    @Mock private ChatClient chatClient;
    @Mock private ConversationRepository conversationRepository;
    @Mock private ConversationMessageRepository messageRepository;
    @Mock private ConversationMapper conversationMapper;
    @Mock private SecurityUtils securityUtils;
    @Mock private ConversationMemoryService memoryService;
    @Mock private EmbeddingService embeddingService;
    @Mock private KnowledgeRetrievalService knowledgeRetrievalService;

    @InjectMocks private AiChatServiceImpl service;

    private User owner;
    private ConversationMessage assistantMessage;

    @BeforeEach
    void setUp() {
        owner = User.builder().cognitoSub("owner-sub").build();

        Conversation conversation = Conversation.builder().user(owner).build();

        assistantMessage = ConversationMessage.builder()
                .id("msg-1")
                .conversation(conversation)
                .role(MessageRole.ASSISTANT)
                .content("H₂O là nước.")
                .modelUsed("inclusionai/ling-3.0-flash:free")
                .build();
    }

    @Test
    @DisplayName("👎 hợp lệ: lưu rating -1 vào message")
    void downvoteIsPersisted() {
        when(messageRepository.findById("msg-1"))
                .thenReturn(Optional.of(assistantMessage));
        when(securityUtils.getCurrentUserCognitoSub()).thenReturn(owner);

        service.rateMessage("msg-1", -1);

        assertThat(assistantMessage.getRating()).isEqualTo(-1);
        verify(messageRepository).save(assistantMessage);
    }

    @Test
    @DisplayName("rating 0 = bỏ chấm: rating về null")
    void zeroClearsRating() {
        assistantMessage.setRating(1);
        when(messageRepository.findById("msg-1"))
                .thenReturn(Optional.of(assistantMessage));
        when(securityUtils.getCurrentUserCognitoSub()).thenReturn(owner);

        service.rateMessage("msg-1", 0);

        assertThat(assistantMessage.getRating()).isNull();
    }

    @Test
    @DisplayName("rating ngoài khoảng [-1,1] bị từ chối")
    void outOfRangeRatingRejected() {
        assertThatThrownBy(() -> service.rateMessage("msg-1", 2))
                .isInstanceOf(AppException.class);
        verify(messageRepository, never()).save(assistantMessage);
    }

    @Test
    @DisplayName("không phải chủ hội thoại thì không chấm được")
    void strangerCannotRate() {
        when(messageRepository.findById("msg-1"))
                .thenReturn(Optional.of(assistantMessage));
        when(securityUtils.getCurrentUserCognitoSub())
                .thenReturn(User.builder().cognitoSub("stranger-sub").build());

        assertThatThrownBy(() -> service.rateMessage("msg-1", 1))
                .isInstanceOf(AppException.class);
        verify(messageRepository, never()).save(assistantMessage);
    }

    @Test
    @DisplayName("USER message không chấm được — chỉ chấm câu trả lời AI")
    void userMessageCannotBeRated() {
        assistantMessage.setRole(MessageRole.USER);
        when(messageRepository.findById("msg-1"))
                .thenReturn(Optional.of(assistantMessage));

        assertThatThrownBy(() -> service.rateMessage("msg-1", 1))
                .isInstanceOf(AppException.class);
    }
}
