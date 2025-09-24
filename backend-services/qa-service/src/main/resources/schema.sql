-- AI问答系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS airline_order_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE airline_order_db;

-- 创建问答记录表
CREATE TABLE IF NOT EXISTS qa_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '问答记录ID',
    question TEXT NOT NULL COMMENT '用户问题',
    answer TEXT NOT NULL COMMENT 'AI回答',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    session_id VARCHAR(100) COMMENT '会话ID',
    question_type VARCHAR(50) DEFAULT 'general' COMMENT '问题类型',
    response_time BIGINT COMMENT '响应时间(毫秒)',
    model_version VARCHAR(50) DEFAULT 'gpt-3.5-turbo' COMMENT 'AI模型版本',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问答记录表';

-- 创建索引
CREATE INDEX idx_qa_user_id ON qa_records(user_id);
CREATE INDEX idx_qa_session_id ON qa_records(session_id);
CREATE INDEX idx_qa_create_time ON qa_records(create_time);
CREATE INDEX idx_qa_question_type ON qa_records(question_type);

-- 插入测试数据
INSERT INTO qa_records (question, answer, user_id, session_id, question_type, response_time, model_version) VALUES
('什么是人工智能？', '人工智能（Artificial Intelligence，AI）是一门研究如何让计算机模拟人类智能的科学技术...', 1, 'session_001', 'general', 1500, 'gpt-3.5-turbo'),
('机器学习的基本原理是什么？', '机器学习是人工智能的一个重要分支，其基本原理是通过算法分析数据...', 1, 'session_001', 'tech', 2000, 'gpt-3.5-turbo'),
('如何开始学习编程？', '学习编程建议从选择一门适合初学者的编程语言开始，比如Python...', 2, 'session_002', 'education', 1800, 'gpt-3.5-turbo');
