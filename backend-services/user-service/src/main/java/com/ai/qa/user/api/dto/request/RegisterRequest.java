package com.ai.qa.user.api.dto.request;

import lombok.Data;

/**
 * 用户注册请求 DTO
 * 封装注册所需字段，含密码二次确认
 *
 * @author Chen Guoping
 * @version 1.0
 */
@Data
public class RegisterRequest {

    /**
     * 用户名（全局唯一）
     * 必填，建议长度 3-20 字符
     *
     * @apiNote 示例：john_doe
     */
    private String username;

    /**
     * 用户昵称（可重复）
     * 选填，建议长度 2-30 字符
     *
     * @apiNote 示例：John Doe
     */
    private String nickname;

    /**
     * 登录密码
     * 必填，建议≥6 位且含字母、数字、特殊字符
     *
     * @apiNote 示例：Password123
     * @security 需加密存储
     */
    private String password;

    /**
     * 确认密码
     * 必填，必须与 password 完全一致
     *
     * @apiNote 示例：Password123
     * @validation 一致性校验
     */
    private String confirmPassword;
}