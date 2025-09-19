package com.ai.qa.service.api.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class QAHistoryDTO {
    private Long id;
    private String userId;
    private String question;
    private String answer;
    private String sessionId;
    private LocalDateTime createTime;
}