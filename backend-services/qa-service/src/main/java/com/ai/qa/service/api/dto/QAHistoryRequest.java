package com.ai.qa.service.api.dto;

import lombok.Data;

@Data
public class QAHistoryRequest {
    private String sessionId;
    private String userId;
}
