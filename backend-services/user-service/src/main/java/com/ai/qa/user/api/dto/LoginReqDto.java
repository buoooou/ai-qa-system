package com.ai.qa.user.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
登录时传输DTO
 */
@Data
public class LoginReqDto {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
