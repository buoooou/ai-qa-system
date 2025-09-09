package com.ai.qa.user.api.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 注册成功响应
 * 在 BaseResponse 基础上返回新创建的用户信息
 *
 * @author Chen Guoping
 * @version 1.0
 */
@Data
public class RegisterResponse extends BaseResponse {

    /** 新建用户 ID */
    private Long userId;

    /** 登录账号 */
    private String username;

    /** 显示昵称 */
    private String nickname;

    /** 账号创建时间（ISO-8601） */
    private LocalDateTime registerTime;
}