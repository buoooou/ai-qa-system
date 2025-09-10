package com.ai.qa.user.domain.entity;

import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;

/*
User表的实体类
 */
@Data
@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(name = "uk_username", columnNames = {"username"})
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;


}
