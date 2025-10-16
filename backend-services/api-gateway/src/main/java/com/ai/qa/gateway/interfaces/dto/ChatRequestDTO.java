package com.ai.qa.gateway.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class ChatRequestDTO {

    @NotNull
    private Long userId;

    private String sessionId;

    @Size(max = 255)
    private String sessionTitle;

    @NotBlank
    private String question;

    private List<MessageDTO> history = Collections.emptyList();

    @Getter
    @Setter
    public static class MessageDTO {
        @NotBlank
        private String role;

        @NotBlank
        private String content;
    }
}
