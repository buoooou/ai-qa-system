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
 * 问答会话持久化对象，对应数据库表 qa_session。
 */
@Data
@Entity
@Table(name= "qa_session")
public class QASessionPO {
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "qa_session_seq")
    @SequenceGenerator(name = "qa_session_seq", sequenceName = "qa_session_id_seq", allocationSize = 1)
    private Long id;
        /**
     * 用户ID
     */
    @Column(name = "user_id")
    private String userId;
    /**
     * 会话ID
     */
    @Column(name = "session_id")
    private String sessionId;
    /**
     * 会话主题
     */
    @Column(name = "topic")
    private String topic;
    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime createtime;
    /**
     * 更新时间
     */
    @Column(name = "update_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime updatetime;
}
