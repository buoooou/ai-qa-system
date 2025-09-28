package com.ai.qa.service.api.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class QAHistoryResponse {
    private final Long id;
    private final Long sessionId;
    private final Long userId;
    private final String question;
    private final String answer;
    private final Integer promptTokens;
    private final Integer completionTokens;
    private final Integer latencyMs;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}