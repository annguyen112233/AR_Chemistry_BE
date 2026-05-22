package com.chemistry.demo.controller.ai;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.ai.AiChatRequest;
import com.chemistry.demo.dto.ai.AiChatResponse;
import com.chemistry.demo.dto.response.AI.ConversationDetailResponse;
import com.chemistry.demo.dto.response.AI.ConversationResponse;
import com.chemistry.demo.services.ai.AiChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiChatService aiChatService;

    @PostMapping("/chat")
    public ApiResponse<AiChatResponse> chatWithAi(@RequestBody @Valid AiChatRequest request) {
        return ApiResponse.<AiChatResponse>ok()
                .data(aiChatService.chatWithAi(request))
                .build();
    }

    @GetMapping("/conversations")
    public ApiResponse<List<ConversationResponse>> getConversations() {
        return ApiResponse.<List<ConversationResponse>>ok()
                .data(aiChatService.getConversations())
                .build();
    }

    @GetMapping("/conversations/{id}")
    public ApiResponse<ConversationDetailResponse> getConversationDetail(@PathVariable String id) {
        return ApiResponse.<ConversationDetailResponse>ok()
                .data(aiChatService.getConversationDetail(id))
                .build();
    }

    @DeleteMapping("/conversations/{id}")
    public ApiResponse<Void> deleteConversation(@PathVariable String id) {
        aiChatService.deleteConversation(id);
        return ApiResponse.<Void>ok().build();
    }
}
