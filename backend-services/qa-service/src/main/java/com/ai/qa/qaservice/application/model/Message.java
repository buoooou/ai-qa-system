package com.ai.qa.qaservice.application.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String role; // 消息角色
    private String content; // 消息内容
    private LocalDateTime timestamp;
}
