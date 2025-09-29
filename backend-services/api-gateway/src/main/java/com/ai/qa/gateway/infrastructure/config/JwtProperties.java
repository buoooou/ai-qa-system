package com.ai.qa.gateway.infrastructure.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Slf4j
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    private String secret;
    private String issuer;

    @PostConstruct
    void logConfiguration() {
        if (secret == null || secret.isBlank()) {
            log.warn("JWT secret is not configured");
        } else {
            log.info("JWT secret configured, length={} characters", secret.length());
        }
        log.info("JWT issuer configured as: {}", issuer);
    }
}
