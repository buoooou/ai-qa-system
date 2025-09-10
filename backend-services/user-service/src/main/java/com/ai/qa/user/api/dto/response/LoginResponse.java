package com.ai.qa.user.api.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 登录成功响应
 * 在 BaseResponse 基础上扩展令牌与用户信息
 *
 * @author Chen Guoping
 * @version 1.0
 */
@Data
public class LoginResponse extends BaseResponse {

    /** JWT 访问令牌 */
    private String token;

    /** 用户 ID */
    private Long userId;

    /** 登录账号 */
    private String username;

    /** 显示昵称 */
    private String nickname;

    /** 本次登录时间（ISO-8601） */
    private LocalDateTime loginTime;
}