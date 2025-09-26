package com.ai.qa.gateway.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {

    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;
}
