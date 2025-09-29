package com.ai.qa.user.api.dto;

import lombok.Data;

@Data
public class AuthResponse {

    private String userId;

    private String token;

}