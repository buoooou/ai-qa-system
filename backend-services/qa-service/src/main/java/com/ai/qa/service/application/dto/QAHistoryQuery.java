package com.ai.qa.service.application.dto;

import lombok.Data;

@Data
public class QAHistoryQuery {
    private String userId;
    private String sessionId;
    private Integer pageSize;
    private Integer pageNumber;
}
