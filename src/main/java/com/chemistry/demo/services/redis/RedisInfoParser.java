package com.chemistry.demo.services.redis;

import com.chemistry.demo.services.redis.model.RedisInfoSections;
import com.chemistry.demo.services.redis.model.RedisTrackingMetrics;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class RedisInfoParser {

    public RedisTrackingMetrics parse(RedisInfoSections sections) {
        return new RedisTrackingMetrics(
                parseLong(sections.memory(), "used_memory"),
                parseLong(sections.clients(), "connected_clients"),
                parseLong(sections.stats(), "total_commands_processed"),
                parseLong(sections.stats(), "keyspace_hits"),
                parseLong(sections.stats(), "keyspace_misses"),
                parseLong(sections.stats(), "expired_keys"),
                parseLong(sections.stats(), "evicted_keys")
        );
    }

    private long parseLong(Properties properties, String key) {
        if (properties == null) {
            return 0L;
        }

        Object value = properties.get(key);
        if (value == null) {
            return 0L;
        }

        try {
            return Long.parseLong(value.toString().trim());
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }
}
