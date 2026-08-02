package com.chemistry.demo.aspect;

/**
 * Gom các chỉ số của một lượt chat AI trong phạm vi một luồng xử lý request,
 * để {@link AiMetricsAspect} tổng hợp và emit một sự kiện AI_CHAT lên ELK.
 * Bước retrieve của RAG sẽ ghi số chunk và điểm cao nhất vào đây.
 */
final class AiCallMetrics {

    private static final ThreadLocal<AiCallMetrics> HOLDER = new ThreadLocal<>();

    private int retrievedChunks = 0;
    private double topScore = 0.0;
    private boolean retrievalRan = false;

    static void begin() {
        HOLDER.set(new AiCallMetrics());
    }

    static AiCallMetrics current() {
        return HOLDER.get();
    }

    static void clear() {
        HOLDER.remove();
    }

    void recordRetrieval(int chunks, double topScore) {
        this.retrievalRan = true;
        this.retrievedChunks = chunks;
        this.topScore = topScore;
    }

    int getRetrievedChunks() {
        return retrievedChunks;
    }

    double getTopScore() {
        return topScore;
    }

    boolean isRetrievalRan() {
        return retrievalRan;
    }
}
