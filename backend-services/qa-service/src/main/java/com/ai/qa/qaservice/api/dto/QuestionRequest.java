package com.ai.qa.qaservice.api.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class QuestionRequest {
    private String conversationId = null; // 可选，为空时创建新对话

    @NotBlank(message = "问题不能为空")
    private String question;
}
