package com.chemistry.demo.dto.ai;

import lombok.*;

/**
 * Kết quả khi tìm thấy một tin nhắn USER cũ có nội dung tương tự.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryMatchResult {
    private String userMessageId;
    private String assistantMessageId;
    private String answer;
    private double similarityScore;
    private String conversationId;
}
