package com.chemistry.demo.dto.response.AI;

import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private String id;
    private String title;
    private String modelUsed;
    private Instant createdAt;
    private Instant updatedAt;
}
