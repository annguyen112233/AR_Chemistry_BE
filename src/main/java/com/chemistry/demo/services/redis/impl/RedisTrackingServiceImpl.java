package com.chemistry.demo.services.redis.impl;

import com.chemistry.demo.dto.response.redis.RedisTrackingResponse;
import com.chemistry.demo.services.redis.RedisInfoParser;
import com.chemistry.demo.services.redis.RedisTrackingResponseFactory;
import com.chemistry.demo.services.redis.RedisTrackingService;
import com.chemistry.demo.services.redis.model.RedisInfoSections;
import com.chemistry.demo.services.redis.model.RedisTrackingMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTrackingServiceImpl implements RedisTrackingService {

    private final RedisConnectionFactory redisConnectionFactory;
    private final RedisInfoParser redisInfoParser;
    private final RedisTrackingResponseFactory redisTrackingResponseFactory;

    @Override
    public RedisTrackingResponse getRedisTracking() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            String ping = connection.ping();
            if (ping == null || !"PONG".equalsIgnoreCase(ping.trim())) {
                String message = "Redis ping failed";
                log.warn("Redis tracking DOWN: ping={}", ping);
                return redisTrackingResponseFactory.down(message);
            }

            RedisInfoSections sections = new RedisInfoSections(
                    safeInfo(connection, "server"),
                    safeInfo(connection, "clients"),
                    safeInfo(connection, "memory"),
                    safeInfo(connection, "stats")
            );
            RedisTrackingMetrics metrics = redisInfoParser.parse(sections);

            log.info("Redis tracking UP: usedMemory={}, connectedClients={}",
                    metrics.usedMemory(),
                    metrics.connectedClients());

            return redisTrackingResponseFactory.success(ping, metrics);
        } catch (Exception ex) {
            String message = shortMessage(ex);
            log.warn("Redis tracking DOWN: {}", message);
            return redisTrackingResponseFactory.down(message);
        }
    }

    private Properties safeInfo(RedisConnection connection, String section) {
        Properties properties = connection.info(section);
        return properties == null ? new Properties() : properties;
    }

    private String shortMessage(Exception ex) {
        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            return "Redis unavailable";
        }
        return message.length() > 120 ? message.substring(0, 120) : message;
    }
}
