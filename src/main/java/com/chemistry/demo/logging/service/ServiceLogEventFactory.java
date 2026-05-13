package com.chemistry.demo.logging.service;

import com.chemistry.demo.logging.PerformanceLog;
import com.chemistry.demo.utils.LogUtil;
import com.chemistry.demo.utils.SanitizerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ServiceLogEventFactory {

    private static final long SLOW_THRESHOLD_MS = 500L;
    private static final String APPLICATION = "AR_Labs";
    private static final String LAYER = "SERVICE";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILED = "FAILED";
    private static final String EVENT_SERVICE_EXECUTION = "SERVICE_EXECUTION";
    private static final String EVENT_SERVICE_EXCEPTION = "SERVICE_EXCEPTION";
    private static final String EVENT_SLOW_SERVICE = "SLOW_SERVICE";

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
