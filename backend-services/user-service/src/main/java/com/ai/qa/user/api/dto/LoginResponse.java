package com.ai.qa.user.api.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    /**
     * 访问令牌
     * 用于访问受保护的API接口
     * 有效期较短（默认2小时）
     */
    @Schema(description = "JWT访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    /**
     * 刷新令牌
     * 用于获取新的访问令牌
     * 有效期较长（默认7天）
     */
    @Schema(description = "JWT访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    /**
     * 令牌类型
     * 固定为"Bearer"
     */
    private String tokenType = "Bearer";
    
    /**
     * 访问令牌过期时间（秒）
     * 前端可以根据此时间进行令牌刷新
     */
    private Long expiresIn;
    
    /**
     * 用户基本信息
     * 不包含密码等敏感信息
     */
    private User user;

    public LoginResponse(User user, String accessToken, String refreshToken, Long expiresIn) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenType = "Bearer";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class User {
        private Long id;
    
        private String username;
    
        private String password;
    
        private String nickname;
    
        private LocalDateTime createTime;
    
        private LocalDateTime updateTime;
    }
}
