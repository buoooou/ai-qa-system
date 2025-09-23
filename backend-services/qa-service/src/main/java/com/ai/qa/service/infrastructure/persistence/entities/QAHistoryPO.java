package com.ai.qa.service.infrastructure.persistence.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 问答历史记录持久化对象，对应数据库表 qa_history。
 */
@Data
@Entity
@Table(name= "qa_history")
public class QAHistoryPO {
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "qa_history_seq")
    @SequenceGenerator(name = "qa_history_seq", sequenceName = "qa_history_id_seq", allocationSize = 1)
    private Long id;
        /**
     * 用户ID
     */
    @Column(name = "user_id")
    private String userId;
    /**
     * 问题内容
     */
    @Column(name = "question")
    private String question;
    /**
     * 回答内容
     */
    @Column(name = "answer")
    private String answer;
    /**
     * 会话ID
     */
    @Column(name = "session_id")
    private String sessionId;
    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime createTime;
    @Column(name = "update_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime updateTime;
}
