package com.ai.qa.service.application.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class QAHistoryDTO {
    Long id;
    Long sessionId;
    Long userId;
    String question;
    String answer;
    Integer promptTokens;
    Integer completionTokens;
    Integer latencyMs;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
