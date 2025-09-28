package com.ai.qa.gateway.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateNicknameGatewayRequest {

    @NotBlank
    private String nickname;
}
