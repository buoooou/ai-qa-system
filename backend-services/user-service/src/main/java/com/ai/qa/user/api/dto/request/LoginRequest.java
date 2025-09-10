package com.ai.qa.user.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "登录请求数据")
public class LoginRequest {
    @NotBlank
    @Schema(description = "用户名", example = "johndoe")
    private String username;

    @NotBlank
    @Schema(description = "密码", example = "password123")
    private String password;
}
