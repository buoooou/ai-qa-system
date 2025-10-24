package com.ai.qa.user.api.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponse {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
