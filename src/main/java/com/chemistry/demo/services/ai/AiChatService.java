package com.chemistry.demo.services.ai;

import com.chemistry.demo.dto.ai.AiChatRequest;
import com.chemistry.demo.dto.ai.AiChatResponse;
import com.chemistry.demo.dto.response.ai.ConversationDetailResponse;
import com.chemistry.demo.dto.response.ai.ConversationResponse;
import java.util.List;

public interface AiChatService {
    AiChatResponse chatWithAi(AiChatRequest request);
    
    List<ConversationResponse> getConversations();
    
    ConversationDetailResponse getConversationDetail(String conversationId);
    
    void deleteConversation(String conversationId);

    /** Chấm câu trả lời AI: 1 = 👍, -1 = 👎, 0 = bỏ chấm. */
    void rateMessage(String messageId, int rating);
}
