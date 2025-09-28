package com.ai.qa.service.api.dto;

import com.ai.qa.service.application.dto.ChatCompletionCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class ChatCompletionRequest {

    @NotNull
    private Long userId;

    private Long sessionId;

    @Size(max = 255)
    private String sessionTitle;

    @NotBlank
    private String question;

    private List<Message> history = Collections.emptyList();

    public ChatCompletionCommand toCommand() {
        return ChatCompletionCommand.builder()
                .userId(userId)
                .sessionId(sessionId)
                .sessionTitle(sessionTitle)
                .question(question)
                .history(history == null ? Collections.emptyList() : history.stream()
                        .map(message -> ChatCompletionCommand.HistoryMessage.builder()
                                .role(message.getRole())
                                .content(message.getContent())
                                .build())
                        .toList())
                .build();
    }

    @Getter
    @Setter
    public static class Message {

        @NotBlank
        private String role;

        @NotBlank
        private String content;
    }
}
