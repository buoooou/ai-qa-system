package com.ai.qa.user.api.dto.request;

import lombok.Data;

/**
 * 用户登录请求 DTO
 * 接收前端传来的账号、密码信息
 *
 * @author Chen Guoping
 * @version 1.0
 */
@Data
public class LoginRequest {

    /**
     * 用户名（唯一标识）
     * 必填，不可为空
     *
     * @apiNote 示例：admin
     */
    private String username;

    /**
     * 登录密码
     * 必填，建议前端先进行强度校验
     *
     * @apiNote 示例：password123
     * @security 需加密传输
     */
    private String password;
}