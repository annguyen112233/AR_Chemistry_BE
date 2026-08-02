package com.chemistry.demo.logging.service;

import com.chemistry.demo.logging.PerformanceLog;
import com.chemistry.demo.utils.LogUtil;
import com.chemistry.demo.utils.SanitizerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ServiceLogEventFactory {

    private static final long SLOW_THRESHOLD_MS = 500L;
    private static final String APPLICATION = "AR_Labs";
    private static final String LAYER = "SERVICE";
    private static final String LAYER_AUDIT = "AUDIT";
    private static final String LAYER_SECURITY = "SECURITY";
    private static final String LAYER_AI = "AI";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_DENIED = "DENIED";
    private static final String EVENT_SERVICE_EXECUTION = "SERVICE_EXECUTION";
    private static final String EVENT_SERVICE_EXCEPTION = "SERVICE_EXCEPTION";
    private static final String EVENT_SLOW_SERVICE = "SLOW_SERVICE";
    private static final String EVENT_ACCESS_GRANTED = "ACCESS_GRANTED";
    private static final String EVENT_ACCESS_DENIED = "ACCESS_DENIED";
    private static final String EVENT_AI_CHAT = "AI_CHAT";

    private final Environment environment;

    public PerformanceLog success(ServiceExecutionContext context, Object result, long startTimeNanos) {
        long durationMs = LogUtil.durationMs(startTimeNanos);
        return base(context, durationMs)
                .toBuilder()
                .level(durationMs > SLOW_THRESHOLD_MS ? "WARN" : "INFO")
                .status(STATUS_SUCCESS)
                .eventType(durationMs > SLOW_THRESHOLD_MS ? EVENT_SLOW_SERVICE : EVENT_SERVICE_EXECUTION)
                .message(durationMs > SLOW_THRESHOLD_MS ? "Slow service detected" : "Service executed")
                .success(true)
                .paramCount(context.paramCount())
                .paramTypes(context.paramTypes())
                .safeParams(context.safeParams().isEmpty() ? null : context.safeParams())
                .resultType(SanitizerUtil.extractResultType(result))
                .resultSize(SanitizerUtil.extractResultSize(result))
                .resultId(SanitizerUtil.extractResultId(result))
                .build();
    }

    public PerformanceLog failure(ServiceExecutionContext context, Throwable ex, long startTimeNanos) {
        long durationMs = LogUtil.durationMs(startTimeNanos);
        return base(context, durationMs)
                .toBuilder()
                .level("ERROR")
                .status(STATUS_FAILED)
                .eventType(EVENT_SERVICE_EXCEPTION)
                .message("Service failed")
                .success(false)
                .errorClass(ex.getClass().getSimpleName())
                .errorMessage(SanitizerUtil.sanitizeErrorMessage(ex.getMessage()))
                .rootCause(SanitizerUtil.sanitizeRootCause(ex))
                .paramCount(context.paramCount())
                .paramTypes(context.paramTypes())
                .safeParams(context.safeParams().isEmpty() ? null : context.safeParams())
                .build();
    }

    /**
     * Sự kiện nghiệp vụ cần audit (thanh toán, kích hoạt kit, cấp quyền, thưởng AR...).
     * eventType chính là action nghiệp vụ để filter trên Kibana.
     */
    public PerformanceLog audit(ServiceExecutionContext context, String action, String actor,
                                boolean success, Object result, long startTimeNanos) {
        long durationMs = LogUtil.durationMs(startTimeNanos);
        return base(context, durationMs)
                .toBuilder()
                .layer(LAYER_AUDIT)
                .level("INFO")
                .status(success ? STATUS_SUCCESS : STATUS_FAILED)
                .eventType(action)
                .message("Audit: " + action)
                .actor(actor)
                .success(success)
                .safeParams(context.safeParams().isEmpty() ? null : context.safeParams())
                .resultType(SanitizerUtil.extractResultType(result))
                .resultId(SanitizerUtil.extractResultId(result))
                .build();
    }

    /**
     * Sự kiện kiểm soát truy cập cho các method được bảo vệ (@PreAuthorize).
     * allowed=false sẽ tạo eventType=ACCESS_DENIED để cảnh báo dò quyền.
     */
    public PerformanceLog security(ServiceExecutionContext context, String actor,
                                   boolean allowed, String reason, long startTimeNanos) {
        long durationMs = LogUtil.durationMs(startTimeNanos);
        return base(context, durationMs)
                .toBuilder()
                .layer(LAYER_SECURITY)
                .level(allowed ? "INFO" : "WARN")
                .status(allowed ? STATUS_SUCCESS : STATUS_DENIED)
                .eventType(allowed ? EVENT_ACCESS_GRANTED : EVENT_ACCESS_DENIED)
                .message(allowed ? "Access granted" : "Access denied: " + reason)
                .actor(actor)
                .success(allowed)
                .build();
    }

    /**
     * Metric cho một lượt chat AI/RAG: cache hit, số chunk retrieve, điểm cao nhất, model...
     * Các chỉ số chi tiết đặt trong safeParams để hiển thị dạng nested JSON trên ELK.
     */
    public PerformanceLog aiChat(ServiceExecutionContext context, String actor,
                                 Map<String, Object> metrics, boolean success, long startTimeNanos) {
        long durationMs = LogUtil.durationMs(startTimeNanos);
        return base(context, durationMs)
                .toBuilder()
                .layer(LAYER_AI)
                .level("INFO")
                .status(success ? STATUS_SUCCESS : STATUS_FAILED)
                .eventType(EVENT_AI_CHAT)
                .message("AI chat handled")
                .actor(actor)
                .success(success)
                .safeParams(metrics == null || metrics.isEmpty() ? null : metrics)
                .build();
    }

    private PerformanceLog base(ServiceExecutionContext context, long durationMs) {
        return PerformanceLog.builder()
                .application(APPLICATION)
                .environment(LogUtil.resolveEnvironment(environment))
                .timestamp(Instant.now())
                .traceId(context.traceId())
                .requestId(context.requestId())
                .serviceName(context.serviceName())
                .module(context.module())
                .layer(LAYER)
                .className(context.serviceName())
                .methodName(context.methodName())
                .durationMs(durationMs)
                .httpMethod(context.httpMethod())
                .endpoint(context.endpoint())
                .build();
    }
}
