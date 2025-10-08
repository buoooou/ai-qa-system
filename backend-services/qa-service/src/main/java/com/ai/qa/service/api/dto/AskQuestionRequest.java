package com.ai.qa.service.api.dto;

import lombok.Data;

@Data
public class AskQuestionRequest {
    private Long userId;
    private String question;
}
