package com.ai.qa.user.domain.entity;

import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户聚合根 - 包含核心业务逻辑
 * 这是业务的核心，包含了所有用户相关的业务规则
 */
@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    // ========== 业务方法 - 核心业务逻辑 ==========
    
    /**
     * 创建新用户 - 业务工厂方法
     * 包含用户创建的所有业务规则
     */
    public static User createNewUser(String username, String rawPassword, String nickname, PasswordEncoder passwordEncoder) {
        // 业务规则验证
        validateUsername(username);
        validatePassword(rawPassword);
        validateNickname(nickname);
        
        User user = new User();
        user.username = username;
        user.password = passwordEncoder.encode(rawPassword);
        user.nickname = nickname;
        
        return user;
    }
    
    /**
     * 验证密码 - 业务逻辑
     */
    public boolean verifyPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.password);
    }
    
    /**
     * 修改密码 - 业务逻辑
     * 包含密码修改的所有业务规则
     */
    public void changePassword(String oldPassword, String newPassword, PasswordEncoder passwordEncoder) {
        // 业务规则：必须验证原密码
        if (!verifyPassword(oldPassword, passwordEncoder)) {
            throw new IllegalArgumentException("原密码错误");
        }
        
        // 业务规则：新密码必须符合要求
        validatePassword(newPassword);
        
        // 业务规则：新密码不能与原密码相同
        if (passwordEncoder.matches(newPassword, this.password)) {
            throw new IllegalArgumentException("新密码不能与原密码相同");
        }
        
        this.password = passwordEncoder.encode(newPassword);
    }
    
    /**
     * 更新昵称 - 业务逻辑
     */
    public void updateNickname(String nickname) {
        validateNickname(nickname);
        this.nickname = nickname;
    }
    
    // ========== 业务规则验证 - 核心业务知识 ==========
    
    /**
     * 用户名验证规则
     */
    private static void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("用户名长度必须在3-50个字符之间");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("用户名只能包含字母、数字和下划线");
        }
    }
    
    /**
     * 密码验证规则
     */
    private static void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("密码长度不能少于6位");
        }
        if (password.length() > 20) {
            throw new IllegalArgumentException("密码长度不能超过20位");
        }
        // 可以添加更多密码复杂度规则
        if (!password.matches(".*[a-zA-Z].*")) {
            throw new IllegalArgumentException("密码必须包含至少一个字母");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("密码必须包含至少一个数字");
        }
    }
    
    /**
     * 昵称验证规则
     */
    private static void validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("昵称不能为空");
        }
        if (nickname.length() > 100) {
            throw new IllegalArgumentException("昵称长度不能超过100个字符");
        }
        // 可以添加昵称的敏感词过滤等规则
    }
}
