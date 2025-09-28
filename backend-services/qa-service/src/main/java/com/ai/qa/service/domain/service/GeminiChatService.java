package com.ai.qa.service.domain.service;

import com.ai.qa.service.application.dto.ChatCompletionCommand;
import com.ai.qa.service.domain.service.StreamingChatResult;

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

    StreamingChatResult streamAnswer(ChatCompletionCommand command);

    record ChatResult(String answer, int promptTokens, int completionTokens, int latencyMs) {
    }
}
