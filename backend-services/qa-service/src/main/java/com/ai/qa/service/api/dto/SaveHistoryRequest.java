package com.ai.qa.service.api.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class SaveHistoryRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "问题不能为空")
    private String question;

    private String answer;
    private String sessionId;
}