package com.chemistry.demo.services.redis.model;

public record RedisTrackingMetrics(
        long usedMemory,
        long connectedClients,
        long totalCommandsProcessed,
        long keyspaceHits,
        long keyspaceMisses,
        long expiredKeys,
        long evictedKeys
) {
}
