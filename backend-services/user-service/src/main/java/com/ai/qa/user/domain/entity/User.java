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
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "username", unique = true, nullable = false, length = 255)
    private String username;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
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
    
    
    /**
     * 业务方法：更新密码
     */
    public void updatePassword(String newPassword) {
        this.password = newPassword;
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 静态工厂方法：创建新用户
     */
    public static User createNewUser(String username, String password, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
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
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 获取创建时间（兼容性方法）
     */
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }
    
    /**
     * 获取更新时间（兼容性方法）
     */
    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }
}
