package com.ai.qa.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Schema(description = "用户注册请求")
public class UserRegisterRequest {
    
    @Schema(description = "用户名", required = true, example = "testuser")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    @Schema(description = "密码", required = true, example = "123456")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    @Schema(description = "用户昵称", example = "测试用户")
    @Size(max = 100, message = "昵称长度不能超过100个字符")
    private String nickname;
}
