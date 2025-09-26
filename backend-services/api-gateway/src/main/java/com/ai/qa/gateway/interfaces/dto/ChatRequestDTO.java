package com.ai.qa.gateway.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequestDTO {

    @NotNull
    private Long userId;

    @NotNull
    private Long sessionId;

    @NotBlank
    private String question;
}
