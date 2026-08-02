package com.chemistry.demo.dto.ai;

import lombok.Builder;

/**
 * Một chunk tri thức được retrieve kèm điểm similarity với câu hỏi.
 */
@Builder
public record RetrievedChunk(
        String sourceType,
        String sourceCode,
        String title,
        String content,
        double score
) {
}
