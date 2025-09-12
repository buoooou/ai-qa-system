package com.ai.qa.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改昵称请求 DTO
 */
@Data
@Schema(description = "修改昵称请求")
public class UpdateNicknameDTO {

    @Schema(description = "新的昵称", required = true, example = "小红")
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;
}
