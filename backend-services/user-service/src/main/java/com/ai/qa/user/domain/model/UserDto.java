package com.ai.qa.user.domain.model;

import lombok.Data;

@Data
public class UserDto {
    private String userid;
    private String username;
    private String password;
    private String nickname;
}
