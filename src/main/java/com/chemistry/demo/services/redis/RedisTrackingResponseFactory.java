package com.chemistry.demo.services.redis;

import com.chemistry.demo.dto.response.redis.RedisTrackingResponse;
import com.chemistry.demo.services.redis.model.RedisTrackingMetrics;
import org.springframework.stereotype.Component;

@Component
public class RedisTrackingResponseFactory {

    public RedisTrackingResponse success(String ping, RedisTrackingMetrics metrics) {
        long hitCount = metrics.keyspaceHits();
        long missCount = metrics.keyspaceMisses();
        long total = hitCount + missCount;
        double hitRate = total == 0L ? 0.0d : (hitCount * 100.0d) / total;

        return RedisTrackingResponse.builder()
                .status("UP")
                .ping(ping)
                .usedMemory(metrics.usedMemory())
                .connectedClients(metrics.connectedClients())
                .totalCommandsProcessed(metrics.totalCommandsProcessed())
                .keyspaceHits(hitCount)
                .keyspaceMisses(missCount)
                .hitRate(hitRate)
                .expiredKeys(metrics.expiredKeys())
                .evictedKeys(metrics.evictedKeys())
                .message("Redis tracking fetched successfully")
                .build();
    }

    public RedisTrackingResponse down(String message) {
        return RedisTrackingResponse.builder()
                .status("DOWN")
                .ping("failed")
                .usedMemory(0L)
                .connectedClients(0L)
                .totalCommandsProcessed(0L)
                .keyspaceHits(0L)
                .keyspaceMisses(0L)
                .hitRate(0.0d)
                .expiredKeys(0L)
                .evictedKeys(0L)
                .message(message)
                .build();
    }
}
