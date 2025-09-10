package com.ai.qa.user.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "登录响应")
@Data
public class LoginResponse {
    
    @ApiModelProperty(value = "JWT访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @ApiModelProperty(value = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";
    
    @ApiModelProperty(value = "令牌过期时间（秒）", example = "86400")
    private Long expiresIn;
    
    @ApiModelProperty(value = "用户信息")
    private UserResponse userInfo;
    
    public LoginResponse(String token, Long expiresIn, UserResponse userInfo) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.userInfo = userInfo;
    }
}