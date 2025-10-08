package com.ai.qa.service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 前端请求保存 QA 历史记录 DTO
 */
@Data
public class SaveHistoryRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "问题不能为空")
    private String question;

    private String answer;
    private String sessionId;
    private String ragAnswer; // 可选 RAG 增强答案
}
