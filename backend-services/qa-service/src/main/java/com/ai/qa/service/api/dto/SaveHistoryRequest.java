package com.ai.qa.service.api.dto;

import lombok.Data;

/**
 * 保存问答历史的请求DTO
 */
@Data
public class SaveHistoryRequest {
    private String userId;
    private String question;
    private String answer;
    private String sessionId;
}