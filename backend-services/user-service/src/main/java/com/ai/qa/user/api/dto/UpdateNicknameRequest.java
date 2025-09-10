package com.ai.qa.user.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "更新昵称请求")
@Data
public class UpdateNicknameRequest {
    
    @ApiModelProperty(value = "新昵称", required = true, example = "新的昵称")
    private String nickname;
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}