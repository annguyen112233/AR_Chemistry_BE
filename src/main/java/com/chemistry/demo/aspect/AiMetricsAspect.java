package com.chemistry.demo.aspect;

import com.chemistry.demo.dto.ai.AiChatResponse;
import com.chemistry.demo.dto.ai.RetrievedChunk;
import com.chemistry.demo.logging.PerformanceLog;
import com.chemistry.demo.logging.service.ServiceExecutionContext;
import com.chemistry.demo.logging.service.ServiceExecutionContextResolver;
import com.chemistry.demo.logging.service.ServiceLogEmitter;
import com.chemistry.demo.logging.service.ServiceLogEventFactory;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Emit metric cho mỗi lượt chat AI/RAG lên ELK (eventType=AI_CHAT):
 * cacheHit (tái dùng câu trả lời), số chunk retrieve được, điểm similarity cao
 * nhất, model, latency. Bao quanh {@code chatWithAi} (gốc) và {@code retrieve}
 * (để lấy chỉ số RAG) của AiChatService/KnowledgeRetrievalService.
 */
@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class AiMetricsAspect {

    private final ServiceExecutionContextResolver contextResolver;
    private final ServiceLogEventFactory eventFactory;
    private final ServiceLogEmitter logEmitter;

    /**
     * Gốc luồng: khởi tạo holder, chạy chat, rồi tổng hợp metric và emit.
     */
    @Around("execution(* com.chemistry.demo.services.ai.AiChatService.chatWithAi(..))")
    public Object measureChat(ProceedingJoinPoint joinPoint) throws Throwable {
        ServiceExecutionContext context = contextResolver.resolve(joinPoint);
        String actor = AuditActor.current();
        long startTime = System.nanoTime();

        AiCallMetrics.begin();
        try {
            Object result = joinPoint.proceed();
            emit(context, actor, result, true, startTime);
            return result;
        } catch (Throwable ex) {
            emit(context, actor, null, false, startTime);
            throw ex;
        } finally {
            AiCallMetrics.clear();
        }
    }

    /**
     * Bước retrieve của RAG: ghi lại số chunk và điểm cao nhất vào holder.
     */
    @Around("execution(* com.chemistry.demo.services.ai.KnowledgeRetrievalService.retrieve(..))")
    public Object captureRetrieval(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        AiCallMetrics metrics = AiCallMetrics.current();
        if (metrics != null && result instanceof List<?> list) {
            double topScore = (!list.isEmpty() && list.get(0) instanceof RetrievedChunk first)
                    ? first.score() : 0.0;
            metrics.recordRetrieval(list.size(), topScore);
        }
        return result;
    }

    private void emit(ServiceExecutionContext context, String actor, Object result,
                      boolean success, long startTime) {
        Map<String, Object> metrics = new LinkedHashMap<>();

        if (result instanceof AiChatResponse response) {
            metrics.put("cacheHit", response.isReusedMemory());
            metrics.put("model", response.getModelUsed());
            if (response.getSimilarityScore() != null) {
                metrics.put("cacheScore", response.getSimilarityScore());
            }
        }

        AiCallMetrics call = AiCallMetrics.current();
        if (call != null && call.isRetrievalRan()) {
            metrics.put("ragUsed", true);
            metrics.put("retrievedChunks", call.getRetrievedChunks());
            metrics.put("topScore", call.getTopScore());
        } else {
            metrics.put("ragUsed", false);
        }

        PerformanceLog event = eventFactory.aiChat(context, actor, metrics, success, startTime);
        logEmitter.emit(event, null);
    }
}
