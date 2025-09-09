package com.ai.qa.user.domain.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体
 * 映射 user_rw 表，维持用户基础信息
 *
 * @author Chen Guoping
 * @version 1.0
 */
@Entity
@Table(name = "user_rw")
@Data
public class User {

    /**
     * 主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 登录账号，全局唯一
     */
    private String username;

    /**
     * 显示昵称，可重复
     */
    private String nickname;

    /**
     * 已加密密码（如 BCrypt）
     */
    private String password;

    /**
     * 创建时间（ISO-8601）
     */
    private LocalDateTime createDate;

    /**
     * 最后更新时间（ISO-8601）
     */
    private LocalDateTime updateDate;
}