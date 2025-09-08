package com.ai.qa.user.dto;

import com.ai.qa.user.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 * 
 * 包含用户信息和JWT令牌信息
 * 用于登录成功后返回给前端的完整认证信息
 * 
 * @author Qiao Zhe
 * @version 1.0
 * @since 2025-09-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    /**
     * 用户基本信息
     * 不包含密码等敏感信息
     */
    private User user;
    
    /**
     * 访问令牌
     * 用于访问受保护的API接口
     * 有效期较短（默认2小时）
     */
    private String accessToken;
    
    /**
     * 刷新令牌
     * 用于获取新的访问令牌
     * 有效期较长（默认7天）
     */
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
     * 构造函数
     * 
     * @param user 用户信息
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     * @param expiresIn 过期时间（秒）
     */
    public LoginResponse(User user, String accessToken, String refreshToken, Long expiresIn) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenType = "Bearer";
    }
}
