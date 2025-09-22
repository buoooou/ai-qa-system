package com.ai.qa.service.application.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class SaveHistoryCommand {
    private Long userId;
    private String question;
    private String answer;
    private String sessionId;
}