package com.ai.qa.user.api.dto;
import lombok.Data;

@Data
public class UserRequestDto {

    private String username;

    private String password;
    
    private String nickname;
    
    private String role;
}
    