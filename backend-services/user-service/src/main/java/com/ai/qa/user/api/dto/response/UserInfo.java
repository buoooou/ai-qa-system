package com.ai.qa.user.api.dto.response;

import lombok.Data;

@Data
public class UserInfo {
    private Long id;
    private String username;
    private String email;
    private String avatar;
}
