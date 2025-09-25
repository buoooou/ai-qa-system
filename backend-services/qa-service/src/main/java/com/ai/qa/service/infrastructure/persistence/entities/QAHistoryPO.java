package com.ai.qa.service.infrastructure.persistence.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity      
@NoArgsConstructor       
@AllArgsConstructor 
@Table(name = "qa_history_ycr")
@Data
public class QAHistoryPO {
    
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
    public QAHistoryPO(Long userId, String question, String answer) {
        this.userId = userId;
        this.question = question;
        this.answer = answer;
        this.createTime = LocalDateTime.now();
    }}
