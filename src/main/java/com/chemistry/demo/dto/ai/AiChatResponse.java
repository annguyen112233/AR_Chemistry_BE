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
}
