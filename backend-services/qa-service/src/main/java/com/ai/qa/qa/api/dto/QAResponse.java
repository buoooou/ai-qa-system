package com.ai.qa.qa.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QAResponse {
    private Long id;
    private String question;
    private String answer;
    private LocalDateTime createTime;
    private String username; // 添加用户名字段
}