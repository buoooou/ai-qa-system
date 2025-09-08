package com.ai.qa.qa.api.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class QARequest {
    @NotBlank(message = "问题不能为空")
    private String question;
    
    private Long userId;
}