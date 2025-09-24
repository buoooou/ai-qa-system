package com.ai.qa.user.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * 对应数据库中的user表，用于存储用户基本信息
 * 包含用户名、密码、创建时间等字段
 * 
 * @author Leon
 * @version 1.0
 * @since 2025-09-06
 */
@Data                    // Lombok注解：自动生成getter、setter、toString、equals、hashCode方法
@NoArgsConstructor       // Lombok注解：生成无参构造函数
@AllArgsConstructor      // Lombok注解：生成全参构造函数
@Entity                  // JPA注解：标识这是一个实体类
@Table(name = "user")  // JPA注解：指定对应的数据库表名
public class User {
    
    /**
     * 用户ID - 主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * 用户名 - 唯一标识
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String userName;
    
    /**
     * 密码 - 加密存储
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    /**
     * 邮箱地址
     */
    @Column(name = "email", length = 100)
    private String email;
    
    /**
     * 用户状态：0-禁用，1-启用
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;
    
    /**
     * 创建时间 - 记录用户注册时间
     */
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
    
    /**
     * 更新时间 - 记录最后修改时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    /**
     * 在持久化之前自动设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 在更新之前自动设置更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 构造函数 - 用于创建新用户
     * 
     * @param userName 用户名
     * @param password 密码
     * @param email 邮箱
     */
    public User(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.status = 1;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
}
