package com.ai.qa.service.api.dto;

import lombok.Data;

@Data
public class QARequest {
    private String sessionId;
    private String userId;
    private String question;
}
