package com.chemistry.demo.controller.ai;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.ai.AiChatRequest;
import com.chemistry.demo.dto.ai.AiChatResponse;
import com.chemistry.demo.dto.ai.RateMessageRequest;
import com.chemistry.demo.dto.response.ai.ConversationDetailResponse;
import com.chemistry.demo.dto.response.ai.ConversationResponse;
import com.chemistry.demo.aspect.AiFlowTrace;
import com.chemistry.demo.services.ai.AiChatService;
import com.chemistry.demo.services.ai.KnowledgeRetrievalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiChatService aiChatService;
    private final KnowledgeRetrievalService knowledgeRetrievalService;

    @PostMapping("/chat")
    @AiFlowTrace("POST /ai/chat")
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

    /** Chấm câu trả lời AI (1 = 👍, -1 = 👎, 0 = bỏ chấm). */
    @PatchMapping("/messages/{id}/rating")
    public ApiResponse<Void> rateMessage(
            @PathVariable String id,
            @RequestBody @Valid RateMessageRequest request) {
        aiChatService.rateMessage(id, request.getRating());
        return ApiResponse.<Void>ok().build();
    }

    /**
     * Vector hoá lại toàn bộ nội dung bài học vào kho tri thức phục vụ RAG.
     * Chỉ ADMIN được phép chạy (thường gọi sau khi import/cập nhật bài học).
     */
    @PostMapping("/knowledge/reindex")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<Map<String, Integer>> reindexKnowledge() {
        int indexed = knowledgeRetrievalService.reindexLessons();
        return ApiResponse.<Map<String, Integer>>ok()
                .data(Map.of("indexedChunks", indexed))
                .build();
    }
}
