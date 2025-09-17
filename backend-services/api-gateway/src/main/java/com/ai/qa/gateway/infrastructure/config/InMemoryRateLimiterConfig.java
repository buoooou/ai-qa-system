package com.ai.qa.gateway.infrastructure.config;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class InMemoryRateLimiterConfig {

    private static final Logger log = LoggerFactory.getLogger(InMemoryRateLimiterConfig.class);
    
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

    /**
     * 内存限流器
     */
    @Bean
    @Primary
    public org.springframework.cloud.gateway.filter.ratelimit.RateLimiter<InMemoryRateLimiterConfig.RateLimiterConfig> inMemoryRateLimiter() {

        final double defaultReplenishRate = 20.0;
        final int defaultBurstCapacity = 200;

        return new org.springframework.cloud.gateway.filter.ratelimit.RateLimiter<RateLimiterConfig>() {

            private final ConcurrentHashMap<String, RateLimiter> limiters = new ConcurrentHashMap<>();
            private final ConcurrentHashMap<String, RateLimiterConfig> configs = new ConcurrentHashMap<>();

            @Override
            public Mono<Response> isAllowed(String routeId, String id) {
                RateLimiterConfig config = configs.getOrDefault(routeId, new RateLimiterConfig(defaultReplenishRate, defaultBurstCapacity));

                RateLimiter limiter = limiters.computeIfAbsent(id, k -> {
                    RateLimiter newLimiter = RateLimiter.create(config.getReplenishRate());
                    newLimiter.tryAcquire(config.getBurstCapacity());
                    return newLimiter;
                });

                boolean allowed = limiter.tryAcquire();

                if (allowed) {
                    log.info("Request ALLOWED. Route: {}, Key: {}", routeId, id);
                    return Mono.just(new Response(true, new HashMap<>()));
                } else {
                    log.warn("Request DENIED (Rate Limited). Route: {}, Key: {}", routeId, id);
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("X-RateLimit-Remaining", "0");
                    headers.put("X-RateLimit-Burst-Capacity", String.valueOf(config.getBurstCapacity()));
                    headers.put("X-RateLimit-Replenish-Rate", String.valueOf(config.getReplenishRate()));
                    return Mono.just(new Response(false, headers));
                }
            }

            @Override
            public Map<String, RateLimiterConfig> getConfig() {
                return this.configs;
            }

            @Override
            public Class<RateLimiterConfig> getConfigClass() {
                return RateLimiterConfig.class;
            }

            @Override
            public RateLimiterConfig newConfig() {
                return new RateLimiterConfig();
            }
        };
    }

    /**
     * 限流配置
     */
    public static class RateLimiterConfig {
        private double replenishRate;
        private int burstCapacity;

        public RateLimiterConfig() {
        }

        public RateLimiterConfig(double replenishRate, int burstCapacity) {
            this.replenishRate = replenishRate;
            this.burstCapacity = burstCapacity;
        }

        public double getReplenishRate() {
            return replenishRate;
        }
        public void setReplenishRate(double replenishRate) {
            this.replenishRate = replenishRate;
        }
        public int getBurstCapacity() {
            return burstCapacity;
        }
        public void setBurstCapacity(int burstCapacity) {
            this.burstCapacity = burstCapacity;
        }
    }
}