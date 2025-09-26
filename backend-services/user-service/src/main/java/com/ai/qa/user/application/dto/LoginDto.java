package com.ai.qa.user.application.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String userid;
    private String username;
    private String nickname;
    private String token;
}
