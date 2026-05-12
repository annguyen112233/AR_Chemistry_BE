package com.chemistry.demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Enterprise Utility for logging, providing data masking and specialized JSON
 * formatting.
 */
@Slf4j
@Component
public class LogUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    private static final Set<String> SENSITIVE_FIELDS = new HashSet<>(Arrays.asList(
            "password", "token", "accessToken", "refreshToken", "secret", "cvv", "pin", "credential"));

    /**
     * Converts an object to a JSON string with sensitive data masked.
     */
    public static String toJson(Object data) {
        if (data == null)
            return "null";
        try {
            return mask(objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            log.warn("Logging serialization failed: {}", e.getMessage());
            return String.valueOf(data);
        }
    }

    /**
     * Masks sensitive JSON fields.
     */
    private static String mask(String json) {
        String masked = json;
        for (String field : SENSITIVE_FIELDS) {
            masked = masked.replaceAll("(\"" + field + "\":\\s*\")[^\"]*(\")", "$1*******$2");
        }
        return masked;
    }
}
