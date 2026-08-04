package com.chemistry.demo.dto.ai;

import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatResponse {
    private String conversationId;
    private String answer;
    private String modelUsed;
    private boolean success;
    private Instant timestamp;
    private boolean reusedMemory;
    private Double similarityScore;

    /**
     * Id của ASSISTANT message vừa trả lời — FE cần để gọi API đánh giá 👍/👎.
     * Với MEMORY_REUSE đây là id của message GỐC, nên 👎 sẽ chặn đúng nguồn cache.
     */
    private String messageId;
}
