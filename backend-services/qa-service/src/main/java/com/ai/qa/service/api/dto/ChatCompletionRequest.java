package com.ai.qa.service.api.dto;

import com.ai.qa.service.application.dto.ChatCompletionCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatCompletionRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long sessionId;

    @NotBlank
    private String question;

    public ChatCompletionCommand toCommand() {
        return ChatCompletionCommand.builder()
                .userId(userId)
                .sessionId(sessionId)
                .question(question)
                .build();
    }
}
