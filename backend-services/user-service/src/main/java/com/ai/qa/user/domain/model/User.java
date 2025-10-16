package com.ai.qa.user.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64, unique = true)
    private String username;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(length = 64)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @Builder.Default
    private UserStatus status = UserStatus.ENABLED;

    private LocalDateTime lastLoginAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static User create(String username, String email, String passwordHash, String nickname) {
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .nickname(nickname)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void changeNickname(String newNickname) {
        if (!StringUtils.hasText(newNickname)) {
            throw new IllegalArgumentException("昵称不能为空。");
        }
        if (newNickname.length() > 50) {
            throw new IllegalArgumentException("昵称长度不能超过50个字符。");
        }
        if (newNickname.equals(this.nickname)) {
            throw new IllegalArgumentException("新昵称不能与旧昵称相同。");
        }
        this.nickname = newNickname;
        touch();
    }

    public void updatePasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        touch();
    }

    public void markLoginNow() {
        this.lastLoginAt = LocalDateTime.now();
        touch();
    }

    public boolean isEnabled() {
        return status == UserStatus.ENABLED;
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum UserRole {
        USER,
        ADMIN
    }

    public enum UserStatus {
        ENABLED,
        DISABLED
    }
}