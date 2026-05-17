package com.chemistry.demo.dto.response.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisTrackingResponse {

    private String status;

    private String ping;

    private long usedMemory;

    private long connectedClients;

    private long totalCommandsProcessed;

    private long keyspaceHits;

    private long keyspaceMisses;

    private double hitRate;

    private long expiredKeys;

    private long evictedKeys;

    private String message;
}
