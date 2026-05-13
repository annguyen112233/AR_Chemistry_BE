package com.chemistry.demo.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Map;

public final class LogUtil {

    private LogUtil() {
    }

    public static long durationMs(long startNanoTime) {
        return Duration.ofNanos(System.nanoTime() - startNanoTime).toMillis();
    }

    public static String currentTraceId() {
        return MDC.get(com.chemistry.demo.config.CorrelationIdFilter.TRACE_ID_KEY);
    }

    public static String currentRequestId() {
        return MDC.get(com.chemistry.demo.config.CorrelationIdFilter.REQUEST_ID_KEY);
    }

    public static String currentHttpMethod() {
        HttpServletRequest request = currentRequest();
        return request != null ? request.getMethod() : null;
    }

    public static String currentEndpoint() {
        HttpServletRequest request = currentRequest();
        return request != null ? request.getRequestURI() : null;
    }

    public static HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    public static String moduleFromClassPackage(Class<?> targetClass) {
        if (targetClass == null || targetClass.getPackageName() == null) {
            return null;
        }

        String packageName = targetClass.getPackageName();
        String marker = ".services.";
        int index = packageName.indexOf(marker);
        if (index < 0) {
            return null;
        }

        String tail = packageName.substring(index + marker.length());
        int dotIndex = tail.indexOf('.');
        return dotIndex >= 0 ? tail.substring(0, dotIndex) : tail;
    }

    public static String simpleClassName(Class<?> targetClass) {
        return targetClass == null ? null : targetClass.getSimpleName();
    }

    public static String resolveEnvironment(org.springframework.core.env.Environment environment) {
        String activeProfile = environment.getProperty("spring.profiles.active");
        if (activeProfile != null && !activeProfile.isBlank()) {
            return activeProfile;
        }
        String[] profiles = environment.getActiveProfiles();
        if (profiles != null && profiles.length > 0) {
            return String.join(",", profiles);
        }
        return "dev";
    }

    public static <K, V> void putAllIfNotNull(Map<K, V> target, Map<K, V> source) {
        if (target == null || source == null) {
            return;
        }
        source.forEach((key, value) -> {
            if (value != null) {
                target.put(key, value);
            }
        });
    }
}
