package com.chemistry.demo.services.admin;

import com.chemistry.demo.dto.response.admin.LogEntryResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Đọc log từ Elasticsearch của bộ ELK (index {@code ar-chemistry-be-logs-*})
 * để hiển thị trong Admin Portal. Backend làm proxy vì:
 * - Elasticsearch không (và không nên) mở ra ngoài internet.
 * - Quyền xem log chốt bằng ROLE_ADMIN ở controller, dùng chung Cognito JWT.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchLogService {

    private static final String INDEX_PATTERN = "ar-chemistry-be-logs-*";
    private static final int MAX_SIZE = 200;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.elk.elasticsearch-url:http://localhost:9200}")
    private String elasticsearchUrl;

    /**
     * Tìm log mới nhất trước, có thể lọc theo level, tìm chữ tự do và giới hạn
     * khoảng thời gian (phút).
     *
     * @return map gồm {@code items} (List&lt;LogEntryResponse&gt;) và
     *         {@code total} (long).
     */
    public Map<String, Object> searchLogs(
            String level,
            String query,
            int minutes,
            int page,
            int size) {

        int safeSize = Math.min(Math.max(size, 1), MAX_SIZE);
        int from = Math.max(page, 0) * safeSize;

        List<Map<String, Object>> must = new ArrayList<>();
        must.add(Map.of("range", Map.of("@timestamp",
                Map.of("gte", "now-" + Math.max(minutes, 1) + "m"))));

        if (level != null && !level.isBlank() && !"ALL".equalsIgnoreCase(level)) {
            // level được logstash lưu dạng text — match, không dùng term keyword
            // để không phụ thuộc mapping.
            must.add(Map.of("match", Map.of("level", level.toUpperCase())));
        }

        if (query != null && !query.isBlank()) {
            must.add(Map.of("multi_match", Map.of(
                    "query", query,
                    "fields", List.of("message", "className", "methodName"),
                    "type", "phrase_prefix")));
        }

        Map<String, Object> body = Map.of(
                "from", from,
                "size", safeSize,
                "sort", List.of(Map.of("@timestamp", Map.of("order", "desc"))),
                "query", Map.of("bool", Map.of("must", must)));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = elasticsearchUrl + "/" + INDEX_PATTERN + "/_search";

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url, new HttpEntity<>(body, headers), String.class);

            return parseResponse(response.getBody());
        } catch (Exception e) {
            log.error("Elasticsearch query failed ({}): {}", url, e.getMessage());
            throw new IllegalStateException(
                    "Không kết nối được Elasticsearch: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> parseResponse(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        JsonNode hits = root.path("hits");

        List<LogEntryResponse> items = new ArrayList<>();
        for (JsonNode hit : hits.path("hits")) {
            JsonNode src = hit.path("_source");
            items.add(LogEntryResponse.builder()
                    .timestamp(src.path("@timestamp").asText(null))
                    .level(src.path("level").asText("INFO"))
                    .message(firstNonBlank(src, "message", "log_message", "event"))
                    .className(firstNonBlank(src, "className", "logger_name", "logger"))
                    .methodName(src.path("methodName").asText(null))
                    .durationMs(src.hasNonNull("durationMs")
                            ? src.path("durationMs").asLong()
                            : null)
                    .correlationId(firstNonBlank(src, "correlationId", "correlation_id"))
                    .build());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("total", hits.path("total").path("value").asLong(items.size()));
        return result;
    }

    private String firstNonBlank(JsonNode src, String... fields) {
        for (String field : fields) {
            String value = src.path(field).asText("");
            if (!value.isBlank()) return value;
        }
        return null;
    }
}
