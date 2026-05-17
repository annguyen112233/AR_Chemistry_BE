package com.chemistry.demo.services.redis.model;

import java.util.Properties;

public record RedisInfoSections(
        Properties server,
        Properties clients,
        Properties memory,
        Properties stats
) {
}
