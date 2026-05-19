package com.chemistry.demo.services.ai;

import com.chemistry.demo.dto.ai.AiChatRequest;
import com.chemistry.demo.dto.ai.AiChatResponse;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

    private final ChatClient chatClient;

    // Danh sách các Model Miễn Phí của OpenRouter để xoay vòng fallback
    private static final List<String> FREE_MODELS = List.of(
            "openrouter/free"
    );

    @Override
    public AiChatResponse chatWithAi(AiChatRequest request) {
        long startTime = System.currentTimeMillis();
        
        for (String model : FREE_MODELS) {
            try {
                log.info("Attempting chat with model: {}", model);
                
                String response = chatClient.prompt()
                        .options(OpenAiChatOptions.builder()
                                .model(model)
                                .temperature(0.7)
                                .build())
                        .system("Bạn là chuyên gia Hóa học. Hãy trả lời ngắn gọn và chính xác.")
                        .user(request.getMessage())
                        .call()
                        .content();

                long duration = System.currentTimeMillis() - startTime;
                log.info("Chat success with model: {}, duration: {}ms", model, duration);

                return AiChatResponse.builder()
                        .answer(response)
                        .modelUsed(model)
                        .success(true)
                        .timestamp(LocalDateTime.now())
                        .build();

            } catch (Exception e) {
                log.warn("Model {} failed: {}. Moving to next fallback...", model, e.getMessage());
            }
        }

        throw new AppException(ErrorCode.AI_SERVICE_ERROR);
    }
}
