package com.ai.qa.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户登录请求")
@Data
public class UserLoginRequest {
    
    @Schema(description = "用户名", required = true, example = "testuser")
    private String username;
    
    @Schema(description = "密码", required = true, example = "password123")
    private String password;
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}