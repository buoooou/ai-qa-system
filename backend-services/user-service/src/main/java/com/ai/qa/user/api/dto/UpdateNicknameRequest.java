package com.ai.qa.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "更新昵称请求")
@Data
public class UpdateNicknameRequest {
    
    @Schema(description = "新昵称", required = true, example = "新的昵称")
    private String nickname;
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}