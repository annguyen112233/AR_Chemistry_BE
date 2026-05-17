package com.chemistry.demo.controller.admin;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.response.redis.RedisTrackingResponse;
import com.chemistry.demo.services.redis.RedisTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/redis")
@RequiredArgsConstructor
public class RedisTrackingController {

    private final RedisTrackingService redisTrackingService;

    @GetMapping("/tracking")
    public ApiResponse<RedisTrackingResponse> getRedisTracking() {
        return ApiResponse.<RedisTrackingResponse>ok()
                .data(redisTrackingService.getRedisTracking())
                .build();
    }
}
