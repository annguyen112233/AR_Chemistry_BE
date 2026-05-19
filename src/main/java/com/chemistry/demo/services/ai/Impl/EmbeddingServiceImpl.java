package com.chemistry.demo.services.ai.Impl;

import com.chemistry.demo.services.ai.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Triển khai EmbeddingService sử dụng Spring AI EmbeddingModel.
 * Tự động sử dụng OpenAI-compatible embedding endpoint đã cấu hình.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService {

    private final EmbeddingModel embeddingModel;

    @Override
    public List<Double> embed(String text) {
        if (text == null || text.isBlank()) {
            log.warn("Cannot embed null or blank text");
            return List.of();
        }

        try {
            float[] embedding = embeddingModel.embed(text);

            List<Double> result = new ArrayList<>(embedding.length);
            for (float v : embedding) {
                result.add((double) v);
            }

            log.debug("Generated embedding with {} dimensions for text: '{}'",
                    result.size(), text.length() > 50 ? text.substring(0, 50) + "..." : text);
            return result;

        } catch (Exception e) {
            log.warn("Embedding generation failed (falling back to no-memory): {}", e.getMessage());
            return List.of();
        }
    }
}
