package com.ai.qa.service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 问答历史实体类
 * 
 * 对应数据库中的qa_history表，用于存储用户的问答记录
 * 包含用户ID、问题、答案、创建时间等字段
 * 
 * @author Qiao Zhe
 * @version 1.0
 * @since 2025-09-06
 */
@Data                    // Lombok注解：自动生成getter、setter、toString、equals、hashCode方法
@NoArgsConstructor       // Lombok注解：生成无参构造函数
@AllArgsConstructor      // Lombok注解：生成全参构造函数
@Entity                  // JPA注解：标识这是一个实体类
@Table(name = "qa_history")  // JPA注解：指定对应的数据库表名
public class QaHistory {
    
    /**
     * 问答记录ID - 主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * 用户ID - 关联用户表
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 用户提出的问题
     */
    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;
    
    /**
     * AI返回的回答
     */
    @Column(name = "answer", columnDefinition = "LONGTEXT")
    private String answer;
    
    /**
     * 创建时间 - 记录问答时间
     */
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
    
    /**
     * 在持久化之前自动设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
    }
    
    /**
     * 构造函数 - 用于创建新的问答记录
     * 
     * @param userId 用户ID
     * @param question 用户问题
     * @param answer AI回答
     */
    public QaHistory(Long userId, String question, String answer) {
        this.userId = userId;
        this.question = question;
        this.answer = answer;
        this.createTime = LocalDateTime.now();
    }
}
