package com.ai.qa.user.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateNickRequest {
    @NotBlank(message = "昵称不能为空")
    private String nickname;
    @NotBlank(message = "用户名不能为空")
    private String username;
}
