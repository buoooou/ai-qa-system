package com.ai.qa.gateway.interfaces.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSessionGatewayRequest {

    @Size(max = 255)
    private String title;
}
