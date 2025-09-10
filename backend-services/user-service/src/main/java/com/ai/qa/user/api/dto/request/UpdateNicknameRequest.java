package com.ai.qa.user.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "修改昵称请求数据")
public class UpdateNicknameRequest {
    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 20, message = "昵称长度必须在2-20个字符之间")
    @Schema(description = "新昵称", example = "张三", minLength = 2, maxLength = 20)
    private String nickname;
}
