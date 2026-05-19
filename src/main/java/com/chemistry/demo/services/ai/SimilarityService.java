package com.chemistry.demo.services.ai;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * Tính Cosine Similarity giữa 2 vector embedding.
 */
@Slf4j
@Service
public class SimilarityService {

    /**
     * Tính cosine similarity giữa 2 vector.
     * @return giá trị từ -1.0 đến 1.0. Giá trị càng gần 1.0 thì càng giống nhau.
     */
    public double cosineSimilarity(List<Double> vectorA, List<Double> vectorB) {
        if (vectorA == null || vectorB == null || vectorA.isEmpty() || vectorB.isEmpty()) {
            log.warn("Embedding vector is null or empty, returning -1");
            return -1.0;
        }
        if (vectorA.size() != vectorB.size()) {
            log.warn("Embedding vector size mismatch: {} vs {}", vectorA.size(), vectorB.size());
            return -1.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.size(); i++) {
            double a = vectorA.get(i);
            double b = vectorB.get(i);
            dotProduct += a * b;
            normA += a * a;
            normB += b * b;
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
