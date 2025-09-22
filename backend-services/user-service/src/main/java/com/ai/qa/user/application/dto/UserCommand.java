package com.ai.qa.user.application.dto;

import lombok.Data;

@Data
public class UserCommand {
    private String username;
    private String password;
    private String nickname;
}
