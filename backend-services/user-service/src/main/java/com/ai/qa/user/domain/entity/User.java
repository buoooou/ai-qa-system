package com.ai.qa.user.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // 已加密

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(nullable = false)
    private LocalDateTime updateTime;

    @Column(nullable = false)
    private String nickname;
}
