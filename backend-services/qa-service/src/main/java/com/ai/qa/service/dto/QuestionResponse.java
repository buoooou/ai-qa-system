package com.ai.qa.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "问答响应DTO")
public class QuestionResponse {

    @ApiModelProperty(value = "问答记录ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "用户问题", example = "什么是人工智能？")
    private String question;

    @ApiModelProperty(value = "AI回答", example = "人工智能是一种模拟人类智能的技术...")
    private String answer;

    @ApiModelProperty(value = "用户ID", example = "1")
    private Long userId;

    @ApiModelProperty(value = "会话ID", example = "session_123")
    private String sessionId;

    @ApiModelProperty(value = "响应时间（毫秒）", example = "1500")
    private Long responseTime;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "问题类型", example = "general")
    private String questionType;

    @ApiModelProperty(value = "AI模型版本", example = "gpt-3.5-turbo")
    private String modelVersion;
}
