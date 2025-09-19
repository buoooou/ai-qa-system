package com.ai.qa.qaservice.api.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class QuestionRequest {
    @NotBlank(message = "问题不能为空")
    private String question;
}
