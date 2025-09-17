package com.ai.qa.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.Size;

@Data
@Schema(description = "修改昵称请求")
public class UpdateNicknameRequest {
    
    @Schema(description = "新昵称", example = "新昵称")
    @Size(max = 100, message = "昵称长度不能超过100个字符")
    private String nickname;
}
