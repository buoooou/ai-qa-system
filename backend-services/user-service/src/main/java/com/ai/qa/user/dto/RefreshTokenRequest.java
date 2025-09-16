package com.ai.qa.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 刷新令牌请求DTO
 *
 * 用于刷新访问令牌的请求参数
 *
 * @author David
 * @version 1.0
 * @since 2025-09-08
 */
@Data
public class RefreshTokenRequest {

    /**
     * 刷新令牌
     * 用于获取新的访问令牌
     */
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}