package com.ai.qa.user.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "注册请求数据")
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    @Schema(description = "用户名", example = "johndoe", minLength = 3, maxLength = 50)
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    @Schema(description = "密码", example = "password123", minLength = 6, maxLength = 40)
    private String password;

}
