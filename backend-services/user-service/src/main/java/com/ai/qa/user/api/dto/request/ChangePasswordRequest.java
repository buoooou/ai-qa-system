package com.ai.qa.user.api.dto.request;

import lombok.Data;

/**
 * 修改密码请求参数
 */
@Data
public class ChangePasswordRequest {

    /**
     * 当前密码
     */
    private String currentPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 确认新密码
     */
    private String confirmNewPassword;
}