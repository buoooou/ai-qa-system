package com.ai.qa.user.api.dto.response;

import lombok.Data;

@Data
public class RegisterResponse {
    private String token;
    private UserInfo user;
}
