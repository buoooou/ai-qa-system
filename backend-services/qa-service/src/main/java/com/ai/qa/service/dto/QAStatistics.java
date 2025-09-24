package com.ai.qa.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "问答统计信息")
public class QAStatistics {

    @ApiModelProperty(value = "总问答数", example = "150")
    private Long totalQuestions;

    @ApiModelProperty(value = "今日问答数", example = "12")
    private Long todayQuestions;

    @ApiModelProperty(value = "平均响应时间（毫秒）", example = "1500")
    private Double averageResponseTime;

    @ApiModelProperty(value = "最受欢迎的问题类型", example = "general")
    private String popularQuestionType;

    @ApiModelProperty(value = "活跃会话数", example = "25")
    private Long activeSessions;

    @ApiModelProperty(value = "用户满意度评分", example = "4.5")
    private Double satisfactionScore;
}
