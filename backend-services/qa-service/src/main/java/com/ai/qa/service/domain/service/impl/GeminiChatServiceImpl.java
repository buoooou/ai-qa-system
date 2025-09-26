package com.ai.qa.service.domain.service.impl;

import com.ai.qa.service.application.dto.ChatCompletionCommand;
import com.ai.qa.service.domain.service.GeminiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Gemini integration service responsible for calling Gemini API to generate chat responses.
 */
@Service
@RequiredArgsConstructor
public class GeminiChatServiceImpl implements GeminiChatService {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    /**
     * Calls Gemini API with the provided chat command and returns the response summary.
     *
     * @param command chat completion command containing user prompt
     * @return chat result with answer, token consumption, and latency
     */
    @Override
    public ChatResult generateAnswer(ChatCompletionCommand command) {
        long start = System.currentTimeMillis();
        // TODO: Replace with real Gemini API call and request/response handling
        String answer = "Gemini response placeholder for question: " + command.getQuestion();
        int latency = (int) (System.currentTimeMillis() - start);
        return new ChatResult(answer, null, null, latency);
    }
}
