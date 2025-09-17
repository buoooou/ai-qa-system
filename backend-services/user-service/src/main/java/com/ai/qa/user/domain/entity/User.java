package com.ai.qa.user.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户领域实体
 * 遵循DDD设计，包含业务逻辑
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @Column(name = "id")
    private String id;
    
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "nickname")
    private String nickname;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        ACTIVE,     // 激活
        INACTIVE,   // 未激活
        LOCKED,     // 锁定
        DELETED     // 已删除
    }
    
    /**
     * 业务方法：检查用户是否可用
     */
    public boolean isAvailable() {
        return status == UserStatus.ACTIVE;
    }
    
    /**
     * 业务方法：激活用户
     */
    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 业务方法：锁定用户
     */
    public void lock() {
        this.status = UserStatus.LOCKED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 业务方法：更新昵称
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 业务方法：更新密码
     */
    public void updatePassword(String newPassword) {
        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 静态工厂方法：创建新用户
     */
    public static User createNewUser(String id, String username, String password, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return User.builder()
                .id(id)
                .username(username)
                .password(passwordEncoder.encode(password))
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * 业务方法：验证密码
     */
    public boolean verifyPassword(String rawPassword, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.password);
    }
    
    /**
     * 业务方法：修改密码
     */
    public void changePassword(String oldPassword, String newPassword, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        if (!verifyPassword(oldPassword, passwordEncoder)) {
            throw new RuntimeException("原密码不正确");
        }
        this.password = passwordEncoder.encode(newPassword);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 获取创建时间（兼容性方法）
     */
    public LocalDateTime getCreateTime() {
        return this.createdAt;
    }
    
    /**
     * 获取更新时间（兼容性方法）
     */
    public LocalDateTime getUpdateTime() {
        return this.updatedAt;
    }
}
