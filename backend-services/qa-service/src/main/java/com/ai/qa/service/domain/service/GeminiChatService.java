package com.ai.qa.service.domain.service;

import com.ai.qa.service.application.dto.ChatCompletionCommand;

/**
 * Domain service abstraction for Gemini chat operations.
 */
public interface GeminiChatService {

    /**
     * Generates an answer through Gemini for the given chat command.
     *
     * @param command chat completion command containing prompt details
     * @return result containing answer and metrics
     */
    ChatResult generateAnswer(ChatCompletionCommand command);

    record ChatResult(String answer, Integer promptTokens, Integer completionTokens, Integer latencyMs) {
    }
}
