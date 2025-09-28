package com.ai.qa.user.application.dto;

import lombok.Data;

@Data
public class SaveRegisterCommand {
    private String username;
    private String password;
    private String email;
    private String avatar;
}
