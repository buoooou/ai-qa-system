package com.ai.qa.service.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "google.ai")
public class GoogleAIProperties {
    private String apiKey;
    private String model;
    private String baseUrl;
}
