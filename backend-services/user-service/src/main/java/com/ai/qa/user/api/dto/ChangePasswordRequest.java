package com.ai.qa.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Schema(description = "修改密码请求")
public class ChangePasswordRequest {
    
    @Schema(description = "原密码", required = true, example = "123456")
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码", required = true, example = "newpass123")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度必须在6-20个字符之间")
    private String newPassword;
}
