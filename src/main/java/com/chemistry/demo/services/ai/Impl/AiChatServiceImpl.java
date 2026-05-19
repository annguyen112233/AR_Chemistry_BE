package com.chemistry.demo.services.ai;

import com.chemistry.demo.dto.ai.AiChatRequest;
import com.chemistry.demo.dto.ai.AiChatResponse;
import com.chemistry.demo.dto.response.ai.ConversationDetailResponse;
import com.chemistry.demo.dto.response.ai.ConversationResponse;
import com.chemistry.demo.entity.Conversation;
import com.chemistry.demo.entity.ConversationMessage;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.MessageRole;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.ErrorCode;
import com.chemistry.demo.mapper.ConversationMapper;
import com.chemistry.demo.repository.ConversationMessageRepository;
import com.chemistry.demo.repository.ConversationRepository;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

    private final ChatClient chatClient;
    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository messageRepository;
    private final ConversationMapper conversationMapper;
    private final SecurityUtils securityUtils;

    private static final List<String> FREE_MODELS = List.of("openrouter/free");

    @Override
    @Transactional
    public AiChatResponse chatWithAi(AiChatRequest request) {
        User currentUser = securityUtils.getCurrentUserCognitoSub();
        
        // 1. Lấy hoặc tạo mới Conversation
        Conversation conversation = getOrCreateConversation(request, currentUser);
        
        // 2. Lưu tin nhắn USER vào DB trước
        saveMessage(conversation, MessageRole.USER, request.getMessage(), null);

        // 3. Gọi AI với lịch sử hội thoại làm ngữ cảnh
        String model = FREE_MODELS.get(0);
        try {
            log.info("Calling AI for conversation: {}", conversation.getId());
            List<Message> messageContext = new ArrayList<>();
            List<ConversationMessage> history = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId());
            for (ConversationMessage msg : history) {
                if (msg.getRole() == MessageRole.USER) {
                    messageContext.add(new UserMessage(msg.getContent()));
                } else if (msg.getRole() == MessageRole.ASSISTANT) {
                    messageContext.add(new AssistantMessage(msg.getContent()));
                }
            }

            String answer = chatClient.prompt()
                    .options(OpenAiChatOptions.builder()
                            .model(model)
                            .temperature(0.7)
                            .build())
                    .system("Bạn là chuyên gia Hóa học. Hãy trả lời ngắn gọn, chính xác dựa trên lịch sử hội thoại.")
                    .messages(messageContext)
                    .call()
                    .content();
            
            // 4. Lưu tin nhắn ASSISTANT vào DB
            saveMessage(conversation, MessageRole.ASSISTANT, answer, model);

            // 5. Cập nhật meta-data cho Conversation
            if (conversation.getTitle() == null || conversation.getTitle().isEmpty()) {
                conversation.setTitle(request.getMessage().length() > 30 ? request.getMessage().substring(0, 30) + "..." : request.getMessage());
            }
            conversation.setModelUsed(model);
            conversation.setUpdatedAt(java.time.Instant.now());
            conversationRepository.save(conversation);

            return AiChatResponse.builder()
                    .conversationId(conversation.getId())
                    .answer(answer)
                    .modelUsed(model)
                    .success(true)
                    .timestamp(java.time.Instant.now())
                    .build();

        } catch (Exception e) {
            log.error("AI call failed for conversation {}: {}", conversation.getId(), e.getMessage());
            throw new AppException(ErrorCode.AI_SERVICE_ERROR);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getConversations() {
        User currentUser = securityUtils.getCurrentUserCognitoSub();
        List<Conversation> conversations = conversationRepository.findByUserCognitoSubOrderByUpdatedAtDesc(currentUser.getCognitoSub());
        return conversations.stream()
                .map(conversationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationDetailResponse getConversationDetail(String conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)); // Cần ErrorCode phù hợp
        
        return conversationMapper.toDetailResponse(conversation);
    }

    @Override
    @Transactional
    public void deleteConversation(String conversationId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        conversationRepository.deleteById(conversationId);
    }

    private Conversation getOrCreateConversation(AiChatRequest request, User user) {
        if (request.getConversationId() != null && !request.getConversationId().isEmpty()) {
            return conversationRepository.findById(request.getConversationId())
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        }
        
        Conversation conversation = Conversation.builder()
                .user(user)
                .build();
        return conversationRepository.save(conversation);
    }

    private void saveMessage(Conversation conversation, MessageRole role, String content, String model) {
        ConversationMessage message = ConversationMessage.builder()
                .conversation(conversation)
                .role(role)
                .content(content)
                .modelUsed(model)
                .build();
        messageRepository.save(message);
    }
}
