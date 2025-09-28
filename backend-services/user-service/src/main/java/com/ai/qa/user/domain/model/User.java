package com.ai.qa.user.domain.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static User createNew(String username, String password, String email, String avatar) {
        User user = new User();
        user.username = username;
        user.password = password;
        user.email = email;
        user.avatar = avatar;
        user.createTime = LocalDateTime.now();
        user.updateTime = LocalDateTime.now();
        return user;
    }
    
    public void updateNick(String username) {
        this.username = username;
        this.updateTime = LocalDateTime.now();
    }
    public String getUsername() {
        return this.username;
    }
}
