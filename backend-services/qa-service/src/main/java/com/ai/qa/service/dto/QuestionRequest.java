package com.ai.qa.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "问答请求DTO")
public class QuestionRequest {

    @ApiModelProperty(value = "问题内容", required = true, example = "什么是人工智能？")
    private String question;

    @ApiModelProperty(value = "用户ID", required = true, example = "1")
    private Long userId;

    @ApiModelProperty(value = "会话ID", example = "session_123")
    private String sessionId;

    @ApiModelProperty(value = "问题类型", example = "general")
    private String questionType;
}
