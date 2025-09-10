package com.ai.qa.user.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_zhuzhiqun")
@Schema(description = "用户信息实体")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @Column(name = "password", nullable = false, length = 255)
    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Column(name = "nickname", length = 50)
    @Schema(description = "昵称", example = "张三")
    private String nickName;

    @Column(name = "create_time", nullable = false, updatable = false)
    @Schema(description = "创建时间", example = "2024-01-01T12:00:00")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Schema(description = "更新时间", example = "2024-01-01T12:00:00")
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
}
