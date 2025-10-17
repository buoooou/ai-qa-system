package com.ai.qa.service.api.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QAHistoryDTO {

    @Schema(description = "问题ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "问题的内容")
    private String question;

    @Schema(description = "AI回答的内容")
    private String answer;

    @Schema(description = "会话ID")
    private String sessionId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
