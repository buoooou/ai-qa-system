package com.ai.qa.service.api.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class QAHistoryDTO {
    private String id;
    private String userId;
    private String question;
    private String answer;
    private LocalDateTime timestamp;
    private String sessionId;
}
