package com.ai.qa.service.application.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ChatCompletionCommand {
    Long userId;
    Long sessionId;
    String question;
}
