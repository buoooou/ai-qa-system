package com.ai.qa.user.api.dto.response;

import lombok.Data;

@Data
public class LoginRsponse {
    private String token;
    private UserInfo user;
}
