package com.services.mafia.miner.configuration;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {
    @Bean
    public RateLimiter rateLimiter() {
        // 2,900 permits per minute = 48.33 requests per second.
        return RateLimiter.create(24);
    }
}
