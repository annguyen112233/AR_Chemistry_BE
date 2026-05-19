package com.chemistry.demo.dto.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequest {
    private String conversationId; // Nullable để tạo chat mới
    
    @NotBlank(message = "Message cannot be blank")
    private String message;
}
