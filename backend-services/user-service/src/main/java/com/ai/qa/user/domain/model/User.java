// com/ai/qa/user/domain/model/User.java
package com.ai.qa.user.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {

    private Long id;
    private final String username;
    private final String password;
    private String nickname;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 构造函数强化校验（替代值对象的约束）
    public User(String username, String password) {
        validateUsername(username);
        validatePassword(password);
        this.username = username;
        this.password = password;
        this.nickname = username; // 默认昵称=用户名
    }

    // 领域行为：修改昵称
    public void updateNickname(String newNickname) {
        validateNickname(newNickname);
        this.nickname = newNickname;
        this.updateTime = LocalDateTime.now();
    }

    // 封装ID设置（仅允许仓储层调用）
    void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("用户ID已分配");
        }
        this.id = id;
    }

    // 时间戳设置（仅允许仓储层调用）
    void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    // 内部校验方法（替代值对象的校验逻辑）
    private void validateUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("用户名格式无效（3-50字符）");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("密码格式无效");
        }
    }

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.length() < 2 || nickname.length() > 20) {
            throw new IllegalArgumentException("昵称长度必须在2-20个字符之间");
        }
    }
}