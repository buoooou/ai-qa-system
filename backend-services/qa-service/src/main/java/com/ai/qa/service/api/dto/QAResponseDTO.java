package com.ai.qa.service.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI 回答的返回结果 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QAResponseDTO {
    private Long userId;
    private String userInfo; // 从 user-service 获取的用户信息
    private String question;
    private String answer;
    private LocalDateTime timestamp;
}
