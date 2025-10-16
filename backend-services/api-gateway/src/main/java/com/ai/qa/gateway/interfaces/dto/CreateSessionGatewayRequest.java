package com.ai.qa.gateway.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSessionGatewayRequest {

    @NotBlank(message = "Session ID is required")
    @Size(max = 64, message = "Session ID length cannot exceed 64 characters")
    private String sessionId;

    @Size(max = 255)
    private String title;
}
