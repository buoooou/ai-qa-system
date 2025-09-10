package com.ai.qa.user.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通用消息响应")
public class MessageResponse {
    @Schema(description = "消息内容", example = "操作成功")
    private String message;
}
