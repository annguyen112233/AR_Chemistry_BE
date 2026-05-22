package com.chemistry.demo.services.upload;

import com.chemistry.demo.enums.UploadType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class UploadPathResolver {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public String generateKey(UploadType uploadType, String originalFileName) {
        String safeFileName = sanitizeFileName(originalFileName);
        String datePath = LocalDate.now().format(DATE_FORMATTER);
        String uuid = UUID.randomUUID().toString();

        // Final format: {folder}/{yyyy}/{MM}/{dd}/{uuid}-{safeFileName}
        return String.format("%s%s/%s-%s",
                uploadType.getFolderPath(),
                datePath,
                uuid,
                safeFileName);
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "unnamed-file";
        }

        // Remove path traversal attempts
        String sanitized = fileName.replace("..", "").replace("/", "").replace("\\", "");

        // Replace dangerous characters with hyphens
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9.\\-_]", "-");

        // Prevent multiple hyphens or trailing hyphens
        sanitized = sanitized.replaceAll("-+", "-").replaceAll("-$", "");

        return sanitized;
    }
}
