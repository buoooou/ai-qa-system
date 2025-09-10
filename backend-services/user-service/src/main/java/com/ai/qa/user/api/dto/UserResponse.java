package com.ai.qa.user.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@ApiModel(description = "用户信息响应")
@Data
public class UserResponse {
    
    @ApiModelProperty(value = "用户ID", example = "1")
    private Long id;
    
    @ApiModelProperty(value = "用户名", example = "testuser")
    private String userName;
    
    @ApiModelProperty(value = "昵称", example = "我的昵称")
    private String nickname;
    
    @ApiModelProperty(value = "创建时间", example = "2023-01-01T10:00:00")
    private LocalDateTime createTime;
    
    @ApiModelProperty(value = "更新时间", example = "2023-01-01T10:00:00")
    private LocalDateTime updateTime;
}