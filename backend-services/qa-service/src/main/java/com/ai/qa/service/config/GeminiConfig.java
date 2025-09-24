package com.ai.qa.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.genai.Client;

@Configuration
public class GeminiConfig {

    @Bean
    Client geminiClient() {
        // 使用注入的 ApacheHttpClient 配置 Client
        return Client.builder().apiKey(System.getenv("GOOGLE_API_KEY")).build();
    }
}
