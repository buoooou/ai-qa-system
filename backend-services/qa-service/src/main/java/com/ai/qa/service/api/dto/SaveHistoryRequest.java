package com.ai.qa.service.api.dto;

import lombok.Data;

@Data
public class SaveHistoryRequest {
    private String userId;
    private String question;
    private String answer;
    private String sessionId;
}
