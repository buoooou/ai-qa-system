package com.ai.qa.qaservice.api.dto;

import lombok.Data;

@Data
public class HistoryRequest {
    private Long userId;
    private Integer limit = 5; // 默认值5
}
