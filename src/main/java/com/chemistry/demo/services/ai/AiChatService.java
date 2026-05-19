package com.chemistry.demo.services.ai;

import com.chemistry.demo.dto.ai.AiChatRequest;
import com.chemistry.demo.dto.ai.AiChatResponse;

public interface AiChatService {
    AiChatResponse chatWithAi(AiChatRequest request);
}
