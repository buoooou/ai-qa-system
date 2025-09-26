package com.ai.qa.service.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.ai.qa.service.api.dto.GeminiRequest;
import com.ai.qa.service.api.dto.GeminiResponse;

// @FeignClient(
//     name = "geminiClient",
//     url = "${GEMINI_API_URL:https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent}?key=${GEMINI_API_KEY}",
//     configuration = GeminiClientConfig.class
// )
@FeignClient(name = "gemini-service", url = "${chat.api.url}")
public interface GeminiClient {
    @PostMapping
    GeminiResponse generateContent(
        @RequestHeader("Content-Type") String contentType,
        @RequestBody GeminiRequest request
    );
}
