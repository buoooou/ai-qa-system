package com.ai.qa.user.api.dto;

import lombok.Data;

/*
登录时传输DTO
 */
@Data
public class LoginReqDto {

    private String username;

    private String password;

}
