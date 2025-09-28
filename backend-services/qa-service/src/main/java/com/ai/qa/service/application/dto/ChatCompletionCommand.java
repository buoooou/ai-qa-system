package com.ai.qa.service.application.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ChatCompletionCommand {
    Long userId;
    Long sessionId;
    String sessionTitle;
    String question;
    @Builder.Default
    List<HistoryMessage> history = List.of();

    public ChatCompletionCommand withSessionId(Long resolvedSessionId) {
        return ChatCompletionCommand.builder()
                .userId(userId)
                .sessionId(resolvedSessionId)
                .sessionTitle(sessionTitle)
                .question(question)
                .history(history)
                .build();
    }

    @Value
    @Builder
    public static class HistoryMessage {
        String role;
        String content;
    }
}
