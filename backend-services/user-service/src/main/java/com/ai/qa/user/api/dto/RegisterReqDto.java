package com.ai.qa.user.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
注册时传输表单DTO
 */
@Data
public class RegisterReqDto {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String nickname;

}
