package com.chemistry.demo.dto.response.ai;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDetailResponse {
    private String id;
    private String title;
    private String modelUsed;
    private List<ConversationMessageResponse> messages;
    private Instant createdAt;
    private Instant updatedAt;
}
