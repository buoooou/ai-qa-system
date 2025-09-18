package com.ai.qa.qaservice.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class QuestionRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "问题不能为空")
    private String question;
}
