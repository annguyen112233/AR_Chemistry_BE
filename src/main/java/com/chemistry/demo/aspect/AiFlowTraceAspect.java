package com.chemistry.demo.aspect;

import com.chemistry.demo.dto.ai.AiChatResponse;
import com.chemistry.demo.dto.ai.MemoryMatchResult;
import com.chemistry.demo.dto.ai.RetrievedChunk;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * Aspect chuyên trace luồng AI/RAG: chat, retrieve, embed, memory search,
 * reindex... In ra từng bước theo dạng cây (có thụt lề theo độ sâu gọi hàm),
 * kèm thời gian và tóm tắt kết quả có ý nghĩa (số chunk retrieve được, điểm
 * similarity cao nhất, có tái dùng cache không, model đã dùng...).
 *
 * <p>Chạy độc lập với {@link LoggingAspect} (log hiệu năng chung ở tầng
 * service). Cố ý loại trừ {@code SimilarityService} vì cosine được gọi trong
 * vòng lặp trên từng chunk, sẽ làm nhiễu log.
 */
@Slf4j
@Aspect
@Component
@Order(0)
public class AiFlowTraceAspect {

    private static final ThreadLocal<Integer> DEPTH = ThreadLocal.withInitial(() -> 0);
    private static final int MAX_ARG_PREVIEW = 60;

    /**
     * Các method trong package services.ai (trừ SimilarityService), HOẶC bất kỳ
     * method nào được đánh dấu {@link AiFlowTrace}.
     */
    @Pointcut("(execution(* com.chemistry.demo.services.ai..*(..))" +
            " && !target(com.chemistry.demo.services.ai.SimilarityService))" +
            " || @annotation(com.chemistry.demo.aspect.AiFlowTrace)")
    public void aiFlowPointcut() {
    }

    @Around("aiFlowPointcut()")
    public Object traceAiFlow(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String label = resolveLabel(signature);
        String traceId = MDC.get("traceId");

        int depth = DEPTH.get();
        String indent = "  ".repeat(depth);

        log.info("[ai-flow{}] {}▶ {}{}",
                traceId != null ? " " + traceId : "", indent, label, argPreview(joinPoint.getArgs()));

        DEPTH.set(depth + 1);
        long startNs = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            long ms = (System.nanoTime() - startNs) / 1_000_000;
            log.info("[ai-flow{}] {}✔ {} ({}ms){}",
                    traceId != null ? " " + traceId : "", indent, label, ms, resultSummary(result));
            return result;
        } catch (Throwable ex) {
            long ms = (System.nanoTime() - startNs) / 1_000_000;
            log.warn("[ai-flow{}] {}✘ {} FAILED ({}ms) - {}: {}",
                    traceId != null ? " " + traceId : "", indent, label, ms,
                    ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        } finally {
            DEPTH.set(depth);
            if (depth == 0) {
                DEPTH.remove();
            }
        }
    }

    private String resolveLabel(MethodSignature signature) {
        Method method = signature.getMethod();
        AiFlowTrace annotation = method.getAnnotation(AiFlowTrace.class);
        if (annotation != null && !annotation.value().isBlank()) {
            return annotation.value();
        }
        return signature.getDeclaringType().getSimpleName() + "." + signature.getName();
    }

    /**
     * Tóm tắt tham số đầu vào (chỉ lấy chuỗi ngắn để nhận diện, tránh log dữ liệu lớn).
     */
    private String argPreview(Object[] args) {
        if (args == null || args.length == 0) return "";
        Object first = args[0];
        if (first instanceof String text) {
            return " q=\"" + truncate(text) + "\"";
        }
        return "";
    }

    /**
     * Trích thông tin có ý nghĩa từ kết quả trả về của từng bước trong luồng.
     */
    private String resultSummary(Object result) {
        if (result == null) return "";

        if (result instanceof AiChatResponse response) {
            return String.format(" -> reusedMemory=%s model=%s score=%s",
                    response.isReusedMemory(),
                    response.getModelUsed(),
                    response.getSimilarityScore() == null
                            ? "-" : String.format("%.4f", response.getSimilarityScore()));
        }

        if (result instanceof Optional<?> optional) {
            if (optional.isEmpty()) return " -> no match";
            if (optional.get() instanceof MemoryMatchResult match) {
                return String.format(" -> memory hit score=%.4f", match.getSimilarityScore());
            }
            return " -> present";
        }

        if (result instanceof List<?> list) {
            if (list.isEmpty()) return " -> 0 chunks";
            if (list.get(0) instanceof RetrievedChunk) {
                double topScore = ((RetrievedChunk) list.get(0)).score();
                return String.format(" -> retrieved %d chunk(s), topScore=%.4f", list.size(), topScore);
            }
            return " -> " + list.size() + " item(s)";
        }

        if (result instanceof Integer count) {
            return " -> " + count + " chunk(s) indexed";
        }

        return "";
    }

    private String truncate(String text) {
        if (text == null) return "";
        String oneLine = text.replaceAll("\\s+", " ").strip();
        return oneLine.length() > MAX_ARG_PREVIEW
                ? oneLine.substring(0, MAX_ARG_PREVIEW) + "..." : oneLine;
    }
}
