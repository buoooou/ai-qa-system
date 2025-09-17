package com.ai.qa.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "用户登录请求")
public class UserLoginRequest {
    
    @Schema(description = "用户名", required = true, example = "testuser")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", required = true, example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;
}
