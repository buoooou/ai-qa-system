package com.ai.qa.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "问答统计信息")
public class QAStatistics {

    @Schema(description = "总问答数", example = "150")
    private Long totalQuestions;

    @Schema(description = "今日问答数", example = "12")
    private Long todayQuestions;

    @Schema(description = "平均响应时间（毫秒）", example = "1500")
    private Double averageResponseTime;

    @Schema(description = "最受欢迎的问题类型", example = "general")
    private String popularQuestionType;

    @Schema(description = "活跃会话数", example = "25")
    private Long activeSessions;

    @Schema(description = "用户满意度评分", example = "4.5")
    private Double satisfactionScore;
}
