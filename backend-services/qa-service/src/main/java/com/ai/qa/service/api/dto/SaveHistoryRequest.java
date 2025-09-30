package com.ai.qa.service.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;


@Data
public class SaveHistoryRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "问题不能为空")
    private String question;

    private String answer;
    private String sessionId;
}
