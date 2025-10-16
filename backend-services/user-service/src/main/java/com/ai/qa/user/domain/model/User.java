package com.ai.qa.user.domain.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.util.StringUtils;

import com.ai.qa.user.common.Constants;

@Entity
@Table(name = "user_hl")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 255)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nickname", nullable = false, length = 255)
    private String nickname;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        LocalDateTime currentTime = LocalDateTime.now();
        if (createTime == null) {
            createTime = currentTime;
        }
        updateTime = currentTime;
    }

    /**
     * 这是核心的业务方法，而不是一个简单的setter.
     * 它封装了更改昵称的所有业务规则。
     *
     * @param newNickname 新的昵称
     */
    public void changeNickname(String newNickname) {
        // 业务规则1: 昵称不能为空或仅包含空白字符
        if (!StringUtils.hasText(newNickname)) {
            throw new IllegalArgumentException(Constants.MSG_NICKNAME_IS_EMPTY);
        }
        // 业务规则2: 昵称长度不能超过200个字符
        if (newNickname.length() > 200) {
            throw new IllegalArgumentException(Constants.MSG_NICKNAME_TOO_LONG);
        }
        // 业务规则3: 昵称不能与现有昵称相同
        if (newNickname.equals(this.nickname)) {
            // 或者可以静默处理，这里选择抛出异常作为示例
            throw new IllegalArgumentException(Constants.MSG_NICKNAME_UNCHANGED);
        }

        this.nickname = newNickname;
    }

    public void registerValidation(String confirmPassword) {
        if (!StringUtils.hasText(this.username)) {
            throw new IllegalArgumentException(Constants.MSG_USERNAME_IS_EMPTY);
        }
        if (!StringUtils.hasText(this.password)) {
            throw new IllegalArgumentException(Constants.MSG_PASSWORD_IS_EMPTY);
        }
        if (!StringUtils.hasText(this.nickname)) {
            throw new IllegalArgumentException(Constants.MSG_NICKNAME_IS_EMPTY);
        }
        if (this.nickname.length() > 200) {
            throw new IllegalArgumentException(Constants.MSG_NICKNAME_TOO_LONG);
        }
        if (confirmPassword == null || !this.password.equals(confirmPassword)) {
            throw new IllegalArgumentException(Constants.MSG_PASSWORD_CONFIRM_ERROR);
        }
    }

    public void loginValidation() {
        if (!StringUtils.hasText(this.username)) {
            throw new IllegalArgumentException(Constants.MSG_USERNAME_IS_EMPTY);
        }
        if (!StringUtils.hasText(this.password)) {
            throw new IllegalArgumentException(Constants.MSG_PASSWORD_IS_EMPTY);
        }
    }
}