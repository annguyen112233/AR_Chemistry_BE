package com.chemistry.demo.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class SanitizerUtil {

    private static final int MAX_STRING_LENGTH = 96;
    private static final int MAX_STACKTRACE_LINES = 18;

    private SanitizerUtil() {
    }

    public static Object sanitizeParam(String name, Object value) {
        if (value == null) {
            return null;
        }

        String lowerName = name == null ? "" : name.toLowerCase(Locale.ROOT);
        if (isSensitiveKey(lowerName)) {
            return "***";
        }

        if (value instanceof CharSequence sequence) {
            return sanitizeString(lowerName, sequence.toString());
        }

        if (value instanceof Number || value instanceof Boolean || value instanceof Enum<?>) {
            return value;
        }

        if (value instanceof TemporalAccessor) {
            return value.toString();
        }

        if (value instanceof Collection<?> collection) {
            return summary(value.getClass().getSimpleName(), collection.size());
        }

        if (value instanceof Map<?, ?> map) {
            return summary(value.getClass().getSimpleName(), map.size());
        }

        if (value.getClass().isArray()) {
            return summary(value.getClass().getComponentType().getSimpleName() + "[]", Array.getLength(value));
        }

        return summarizeObject(lowerName, value);
    }

    public static String sanitizeErrorMessage(String message) {
        if (message == null) {
            return null;
        }
        String sanitized = maskEmails(message);
        sanitized = sanitized.replaceAll("(?i)(password|token|secret|authorization|credential)\\s*[:=]\\s*([^,;\\]\\}\\s]+)", "$1=***");
        return sanitizeText(sanitized, 240);
    }

    public static String sanitizeRootCause(Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        Throwable root = throwable;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }

        String className = root.getClass().getSimpleName();
        String message = sanitizeErrorMessage(root.getMessage());
        if (message == null || message.isBlank()) {
            return className;
        }
        return className + ": " + message;
    }

    public static Object extractResultId(Object result) {
        if (result == null || result instanceof Collection<?> || result instanceof Map<?, ?> || result.getClass().isArray()) {
            return null;
        }

        Object id = invokeNoArg(result, "getId");
        if (id == null) {
            id = invokeNoArg(result, "id");
        }
        if (id == null) {
            id = readField(result, "id");
        }

        return sanitizeResultId(id);
    }

    public static String extractResultType(Object result) {
        if (result == null) {
            return null;
        }
        if (result instanceof Collection<?>) {
            return "Collection";
        }
        if (result instanceof Map<?, ?>) {
            return "Map";
        }
        if (result.getClass().isArray()) {
            return result.getClass().getComponentType().getSimpleName() + "[]";
        }
        return result.getClass().getSimpleName();
    }

    public static Long extractResultSize(Object result) {
        if (result instanceof Collection<?> collection) {
            return (long) collection.size();
        }
        if (result instanceof Map<?, ?> map) {
            return (long) map.size();
        }
        if (result != null && result.getClass().isArray()) {
            return (long) Array.getLength(result);
        }
        return null;
    }

    public static Map<String, Object> safeParamMap(String[] parameterNames, Object[] args) {
        Map<String, Object> safeParams = new LinkedHashMap<>();
        if (args == null || args.length == 0) {
            return safeParams;
        }

        for (int i = 0; i < args.length; i++) {
            String paramName = parameterNames != null && parameterNames.length > i && parameterNames[i] != null
                    ? parameterNames[i]
                    : "arg" + i;
            safeParams.put(paramName, sanitizeParam(paramName, args[i]));
        }

        return safeParams;
    }

    public static String shortStackTrace(Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        StackTraceElement[] stackTrace = throwable.getStackTrace();
        int limit = Math.min(stackTrace.length, MAX_STACKTRACE_LINES);
        StringBuilder builder = new StringBuilder();
        builder.append(throwable.getClass().getSimpleName())
                .append(": ")
                .append(sanitizeText(throwable.getMessage(), 160));
        for (int i = 0; i < limit; i++) {
            builder.append("\n at ").append(stackTrace[i]);
        }
        if (stackTrace.length > limit) {
            builder.append("\n ...").append(stackTrace.length - limit).append(" more");
        }
        return builder.toString();
    }

    private static Object summarizeObject(String lowerName, Object value) {
        if (looksLikeSimpleSafeBean(value)) {
            Map<String, Object> preview = new LinkedHashMap<>();
            for (Field field : value.getClass().getDeclaredFields()) {
                if (preview.size() >= 3) {
                    break;
                }
                if (field.isSynthetic()) {
                    continue;
                }
                String fieldName = field.getName();
                String lowerField = fieldName.toLowerCase(Locale.ROOT);
                if (isSensitiveKey(lowerField)) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(value);
                    if (fieldValue != null && isSimpleValue(fieldValue)) {
                        preview.put(fieldName, sanitizeParam(fieldName, fieldValue));
                    }
                } catch (IllegalAccessException ignored) {
                }
            }
            if (!preview.isEmpty()) {
                return preview;
            }
        }

        String simpleName = value.getClass().getSimpleName();
        if (simpleName == null || simpleName.isBlank()) {
            simpleName = value.getClass().getName();
        }
        return summary(simpleName, null);
    }

    private static boolean looksLikeSimpleSafeBean(Object value) {
        String packageName = value.getClass().getPackageName();
        return packageName != null && packageName.startsWith("com.chemistry.demo.dto");
    }

    private static boolean isSimpleValue(Object value) {
        return value instanceof CharSequence
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Enum<?>
                || value instanceof TemporalAccessor;
    }

    private static Object sanitizeResultId(Object id) {
        if (id == null) {
            return null;
        }
        if (id instanceof CharSequence sequence) {
            return sanitizeText(sequence.toString(), 64);
        }
        return id;
    }

    private static Object invokeNoArg(Object target, String methodName) {
        try {
            Method method = target.getClass().getMethod(methodName);
            return method.invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Object readField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static boolean isSensitiveKey(String lowerName) {
        return lowerName.contains("password")
                || lowerName.contains("token")
                || lowerName.contains("secret")
                || lowerName.contains("authorization")
                || lowerName.contains("credential");
    }

    private static String sanitizeString(String lowerName, String value) {
        if (value == null) {
            return null;
        }
        if (isSensitiveKey(lowerName)) {
            return "***";
        }
        if (looksLikeEmail(value)) {
            return maskEmail(value);
        }
        if (looksLikeToken(value)) {
            return "***";
        }
        return sanitizeText(value, MAX_STRING_LENGTH);
    }

    private static boolean looksLikeEmail(String value) {
        return value.contains("@") && value.indexOf('@') > 0;
    }

    private static boolean looksLikeToken(String value) {
        String normalized = value.replaceAll("\\s+", "");
        return normalized.length() >= 120 && normalized.matches("^[A-Za-z0-9+/=_-]+$");
    }

    private static String maskEmails(String value) {
        return value.replaceAll("([A-Za-z0-9._%+-])([A-Za-z0-9._%+-]*)(@[A-Za-z0-9.-]+\\.[A-Za-z]{2,})", "$1***$3");
    }

    private static String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1) {
            return "***";
        }
        String domain = email.substring(atIndex);
        return "a***" + domain;
    }

    private static String sanitizeText(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            return trimmed.substring(0, maxLength) + "...";
        }
        return trimmed;
    }

    private static String summary(String type, Integer size) {
        if (size == null) {
            return type;
        }
        return type + "[size=" + size + "]";
    }
}
