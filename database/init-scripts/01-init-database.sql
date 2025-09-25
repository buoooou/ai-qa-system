-- AI问答系统数据库初始化脚本
-- 创建一个名为 'ai_qa_system' 的数据库，如果它不存在的话
CREATE DATABASE IF NOT EXISTS `ai_qa_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建Nacos配置数据库
CREATE DATABASE IF NOT EXISTS `nacos_config` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 切换到该数据库
USE `ai_qa_system`;

-- ----------------------------
-- 用户表 (user)
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(255) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '加密后的密码',
  `nick` VARCHAR(255) DEFAULT NULL COMMENT '用户昵称',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- 问答历史表 (qa_history)
-- ----------------------------
DROP TABLE IF EXISTS `qa_history`;
CREATE TABLE `qa_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `question` TEXT NOT NULL COMMENT '用户提出的问题',
  `answer` LONGTEXT COMMENT 'AI返回的回答',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_qa_history_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问答历史表';

-- ----------------------------
-- 插入测试数据
-- ----------------------------
-- 插入测试用户（密码为123456的BCrypt加密结果）
INSERT INTO `user` (`username`, `password`, `nick`) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '管理员'),
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '测试用户');

-- 插入测试问答记录
INSERT INTO `qa_history` (`user_id`, `question`, `answer`) VALUES 
(1, '什么是人工智能？', '人工智能（Artificial Intelligence，AI）是计算机科学的一个分支，致力于创建能够执行通常需要人类智能的任务的系统。'),
(1, 'Spring Boot有什么优势？', 'Spring Boot简化了Spring应用的开发，提供了自动配置、内嵌服务器、生产就绪的功能等特性。'),
(2, '如何学习编程？', '学习编程建议从基础语法开始，多练习项目，阅读优秀代码，参与开源项目，持续学习新技术。');

-- 创建索引以提高查询性能
CREATE INDEX `idx_user_username` ON `user` (`username`);
CREATE INDEX `idx_qa_history_user_create` ON `qa_history` (`user_id`, `create_time`);

-- 显示创建的表
SHOW TABLES;

-- 显示用户表结构
DESCRIBE `user`;

-- 显示问答历史表结构
DESCRIBE `qa_history`;

SELECT 'Database initialization completed successfully!' AS message;