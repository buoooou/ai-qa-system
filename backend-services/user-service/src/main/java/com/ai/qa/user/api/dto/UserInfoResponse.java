package com.ai.qa.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户信息响应")
public class UserInfoResponse {
    
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "testuser")
    private String username;
    
    @Schema(description = "用户昵称", example = "测试用户")
    private String nickname;
    
    @Schema(description = "创建时间", example = "2025-09-11T10:00:00")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间", example = "2025-09-11T10:00:00")
    private LocalDateTime updateTime;
}
