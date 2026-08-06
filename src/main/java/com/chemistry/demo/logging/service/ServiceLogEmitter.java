package com.chemistry.demo.logging.service;

import com.chemistry.demo.entity.SystemLog;
import com.chemistry.demo.logging.PerformanceLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

/**
 * Điểm phát log duy nhất cho mọi aspect (performance, audit, security, AI).
 * Log ra console như cũ, đồng thời lưu vào bảng {@code system_logs} trong
 * Postgres (thay cho ELK đã gỡ) để Admin Portal đọc lại được.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ServiceLogEmitter {

    private final SystemLogPersister persister;

    public void emit(PerformanceLog event, Throwable throwable) {
        if (throwable != null) {
            log.error(event.getMessage(), throwable);
        } else if ("WARN".equals(event.getLevel())) {
            log.warn(event.getMessage());
        } else {
            log.info(event.getMessage());
        }

        persister.persist(toEntity(event, throwable));
    }

    private SystemLog toEntity(PerformanceLog event, Throwable throwable) {
        String level = throwable != null
                ? "ERROR"
                : (event.getLevel() != null ? event.getLevel() : "INFO");

        return SystemLog.builder()
                .loggedAt(event.getTimestamp() != null
                        ? event.getTimestamp()
                        : Instant.now())
                .level(level)
                .message(event.getMessage())
                .className(truncate(event.getClassName(), 255))
                .methodName(truncate(event.getMethodName(), 150))
                .durationMs(event.getDurationMs())
                .eventType(truncate(event.getEventType(), 100))
                .actor(truncate(event.getActor(), 150))
                .endpoint(truncate(event.getEndpoint(), 255))
                .httpMethod(truncate(event.getHttpMethod(), 10))
                .success(event.getSuccess())
                .errorClass(truncate(
                        throwable != null
                                ? throwable.getClass().getName()
                                : event.getErrorClass(),
                        255))
                .errorMessage(throwable != null
                        ? throwable.getMessage()
                        : event.getErrorMessage())
                .stackTrace(stackTraceOf(throwable))
                .correlationId(truncate(
                        event.getTraceId() != null
                                ? event.getTraceId()
                                : event.getRequestId(),
                        100))
                .build();
    }

    private String stackTraceOf(Throwable throwable) {
        if (throwable == null) return null;
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        String trace = writer.toString();
        // Giới hạn để một exception dây chuyền không nhét cả trăm KB vào DB.
        return trace.length() > 8000 ? trace.substring(0, 8000) : trace;
    }

    private String truncate(String value, int max) {
        if (value == null) return null;
        return value.length() > max ? value.substring(0, max) : value;
    }
}
