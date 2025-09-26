package com.ai.qa.gateway.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QAHistoryResponseDTO {
    private Long id;
    private Long sessionId;
    private Long userId;
    private String question;
    private String answer;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer latencyMs;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
