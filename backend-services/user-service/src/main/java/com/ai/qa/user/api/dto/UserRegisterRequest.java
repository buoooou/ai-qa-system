package com.ai.qa.user.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "用户注册请求")
@Data
public class UserRegisterRequest {
    
    @ApiModelProperty(value = "用户名", required = true, example = "newuser")
    private String username;
    
    @ApiModelProperty(value = "密码", required = true, example = "password123")
    private String password;
    
    @ApiModelProperty(value = "昵称", required = false, example = "我的昵称")
    private String nickname;
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}