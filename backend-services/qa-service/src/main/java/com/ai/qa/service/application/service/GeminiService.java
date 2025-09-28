package com.ai.qa.service.application.service;

import com.ai.qa.service.api.dto.GeminiRequest;
import com.ai.qa.service.api.dto.GeminiResponse;
import com.ai.qa.service.infrastructure.feign.GeminiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GeminiService {
    private final GeminiClient geminiClient;

    public String getChatReply(String userMessage) {
        GeminiRequest.Message userApiMessage = new GeminiRequest.Message("user", userMessage);
        GeminiRequest requestBody = new GeminiRequest(
            "gemini-pro",
            Collections.singletonList(userApiMessage),
            0.7
        );

        try {
            GeminiResponse response = geminiClient.generateContent(
                "application/json",
                requestBody
            );

            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }
            return "未能获取到AI的回复。";
        } catch (Exception e) {
            throw new RuntimeException("调用 Gemini API 失败: " + e.getMessage());
        }
    }
}