package com.chemistry.demo.dto.response.ai;

import com.chemistry.demo.enums.MessageRole;
import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMessageResponse {
    private String id;
    private MessageRole role;
    private String content;
    private String modelUsed;
    private Instant createdAt;
    private Integer rating;
}
