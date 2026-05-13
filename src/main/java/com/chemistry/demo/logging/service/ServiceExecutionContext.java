package com.chemistry.demo.logging.service;

import java.util.List;
import java.util.Map;

public record ServiceExecutionContext(
        Class<?> targetClass,
        String serviceName,
        String module,
        String methodName,
        String traceId,
        String requestId,
        String httpMethod,
        String endpoint,
        int paramCount,
        List<String> paramTypes,
        Map<String, Object> safeParams
) {
}
