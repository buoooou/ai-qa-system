package com.ai.qa.service.application.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SaveHistoryCommand {
    Long sessionId;
    Long userId;
    String question;
    String answer;
    Integer promptTokens;
    Integer completionTokens;
    Integer latencyMs;
}
