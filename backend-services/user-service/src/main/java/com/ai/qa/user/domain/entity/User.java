package com.ai.qa.user.domain.entity;

import lombok.Data;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    private Long id;
    private String username;
    private String password;
    private String nick;
    @Column(name = "update_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime createTime;
    @Column(name = "creation_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime updateTime;


}