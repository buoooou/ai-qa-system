package com.ai.qa.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "加密后的密码")
    private String password;

    @Schema(description = "加密后的确认密码")
    private String confirmPassword;

    @Schema(description = "昵称")
    private String nickname;

}
