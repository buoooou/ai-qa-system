package com.ai.qa.user.api.dto;

import lombok.Data;

/*
注册时传输表单DTO
 */
@Data
public class RegisterReqDto {

    private String username;

    private String password;

}
