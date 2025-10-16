-- 创建一个名为 'ai_qa_system' 的数据库，如果它不存在的话
CREATE DATABASE IF NOT EXISTS `ai_qa_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 切换到该数据库
USE `ai_qa_system`;

-- ----------------------------
-- 用户相关表
-- ----------------------------
DROP TABLE IF EXISTS `suggestion`;
DROP TABLE IF EXISTS `document`;
DROP TABLE IF EXISTS `qa_history`;
DROP TABLE IF EXISTS `qa_session`;
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(64) NOT NULL COMMENT '登录用户名',
  `email` VARCHAR(255) NOT NULL COMMENT '用户邮箱',
  `password_hash` VARCHAR(255) NOT NULL COMMENT 'BCrypt 加密后的密码',
  `nickname` VARCHAR(64) DEFAULT NULL COMMENT '显示昵称',
  `role` ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER' COMMENT '角色',
  `status` ENUM('ENABLED','DISABLED') NOT NULL DEFAULT 'ENABLED' COMMENT '账号状态',
  `last_login_at` DATETIME DEFAULT NULL COMMENT '最近登录时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_username` (`username`),
  UNIQUE KEY `uk_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE `qa_session` (
  `id` VARCHAR(64) NOT NULL COMMENT '会话ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(255) NOT NULL DEFAULT 'New Conversation' COMMENT '会话标题',
  `status` ENUM('ACTIVE','ARCHIVED') NOT NULL DEFAULT 'ACTIVE' COMMENT '会话状态',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_session_user_status` (`user_id`,`status`),
  CONSTRAINT `fk_session_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话表';

-- ----------------------------
-- 问答历史表 (qa_history)
-- ----------------------------
CREATE TABLE `qa_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '问答记录ID',
  `session_id` VARCHAR(64) NOT NULL COMMENT '会话ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `question` TEXT NOT NULL COMMENT '用户提问',
  `answer` LONGTEXT COMMENT 'Gemini 回复',
  `prompt_tokens` INT DEFAULT NULL COMMENT 'Prompt tokens 消耗',
  `completion_tokens` INT DEFAULT NULL COMMENT 'Completion tokens 消耗',
  `latency_ms` INT DEFAULT NULL COMMENT '响应延迟毫秒数',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_history_session_created` (`session_id`,`created_at`),
  KEY `idx_history_user_created` (`user_id`,`created_at`),
  CONSTRAINT `fk_history_session` FOREIGN KEY (`session_id`) REFERENCES `qa_session` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_history_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问答历史表';

CREATE TABLE `document` (
  `document_id` CHAR(36) NOT NULL COMMENT '文档ID',
  `created_at` DATETIME NOT NULL COMMENT '版本创建时间',
  `title` VARCHAR(255) NOT NULL COMMENT '标题',
  `content` LONGTEXT COMMENT '内容',
  `kind` ENUM('TEXT','CODE','IMAGE','SHEET') NOT NULL DEFAULT 'TEXT' COMMENT '类型',
  `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
  PRIMARY KEY (`document_id`, `created_at`),
  KEY `idx_document_user` (`user_id`),
  CONSTRAINT `fk_document_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='版本化文档表';

CREATE TABLE `suggestion` (
  `id` CHAR(36) NOT NULL COMMENT '建议ID',
  `document_id` CHAR(36) NOT NULL COMMENT '文档ID',
  `document_created_at` DATETIME NOT NULL COMMENT '文档版本创建时间',
  `original_text` TEXT NOT NULL COMMENT '原始文本',
  `suggested_text` TEXT NOT NULL COMMENT '建议文本',
  `description` TEXT COMMENT '建议说明',
  `is_resolved` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已处理',
  `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_suggestion_doc` (`document_id`, `document_created_at`),
  CONSTRAINT `fk_suggestion_document` FOREIGN KEY (`document_id`, `document_created_at`) REFERENCES `document` (`document_id`, `created_at`) ON DELETE CASCADE,
  CONSTRAINT `fk_suggestion_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档建议表';

-- 插入一些测试数据 (可选)
INSERT INTO `users` (`username`, `email`, `password_hash`, `nickname`, `role`)
VALUES ('demo', 'demo@cn.ibm.com', '$2a$10$AnURfk/9yRtRWNTUEMSlPuq0iCzO2M1rNT0zWoQJMnaZKoxTg8MKi', 'Demo User', 'USER');