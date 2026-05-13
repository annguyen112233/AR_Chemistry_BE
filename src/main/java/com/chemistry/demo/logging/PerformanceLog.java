package com.chemistry.demo.logging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceLog {

    private String application;
    private String environment;
    private String level;
    private Instant timestamp;
    private String traceId;
    private String requestId;
    private String serviceName;
    private String module;
    private String layer;
    private String className;
    private String methodName;
    private Long durationMs;
    private String status;
    private String eventType;
    private String errorClass;
    private String errorMessage;
    private String rootCause;
    private String httpMethod;
    private String endpoint;
    private String message;
    private String resultType;
    private Long resultSize;
    private Object resultId;
    private Boolean success;
    private Integer paramCount;
    private List<String> paramTypes;
    private Map<String, Object> safeParams;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new LinkedHashMap<>();
        put(values, "application", application);
        put(values, "environment", environment);
        put(values, "serviceName", serviceName);
        put(values, "module", module);
        put(values, "layer", layer);
        put(values, "className", className);
        put(values, "methodName", methodName);
        put(values, "durationMs", durationMs);
        put(values, "status", status);
        put(values, "eventType", eventType);
        put(values, "errorClass", errorClass);
        put(values, "errorMessage", errorMessage);
        put(values, "rootCause", rootCause);
        put(values, "httpMethod", httpMethod);
        put(values, "endpoint", endpoint);
        put(values, "resultType", resultType);
        put(values, "resultSize", resultSize);
        put(values, "resultId", resultId);
        put(values, "success", success);
        put(values, "paramCount", paramCount);
        put(values, "paramTypes", paramTypes);
        put(values, "safeParams", safeParams);
        return values;
    }

    private static void put(Map<String, Object> values, String key, Object value) {
        if (value != null) {
            values.put(key, value);
        }
    }
}
