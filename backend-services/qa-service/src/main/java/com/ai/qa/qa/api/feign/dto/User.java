package com.ai.qa.qa.api.feign.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nick;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}