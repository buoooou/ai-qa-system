package com.ai.qa.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录响应")
public class LoginResponse {
    
    @Schema(description = "访问令牌", example = "token_1234567890_1234567890")
    private String token;
    
    @Schema(description = "用户信息")
    private UserInfoResponse userInfo;
}
