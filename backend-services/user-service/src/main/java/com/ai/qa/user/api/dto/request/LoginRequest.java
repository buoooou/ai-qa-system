package com.ai.qa.user.api.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 10, message = "用户名长度必须在4-10位之间")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 16, message = "密码长度必须在8-16位之间")
    private String password;
}
