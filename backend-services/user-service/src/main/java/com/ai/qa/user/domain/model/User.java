package com.ai.qa.user.domain.model;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String username;
    private String password;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static User createNew(String username, String password) {
        User user = new User();
        user.username = username;
        user.password = password;
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
