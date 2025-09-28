-- 创建一个名为 'ai_qa_system' 的数据库
CREATE DATABASE ai_qa_system;

-- 连接到该数据库
\c ai_qa_system;

-- ----------------------------
-- 用户表 (user)
-- ----------------------------
DROP TABLE IF EXISTS "users";
CREATE TABLE "users" (
  "id" BIGSERIAL PRIMARY KEY,
  "username" VARCHAR(255) NOT NULL,
  "password" VARCHAR(255) NOT NULL,
  "nick" VARCHAR(255) NOT NULL,
  "create_time" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "update_time" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 添加注释
COMMENT ON TABLE "users" IS '用户表';
COMMENT ON COLUMN "users"."id" IS '主键ID';
COMMENT ON COLUMN "users"."username" IS '用户名';
COMMENT ON COLUMN "users"."nick" IS '用户名';
COMMENT ON COLUMN "users"."password" IS '加密后的密码';
COMMENT ON COLUMN "users"."create_time" IS '创建时间';
COMMENT ON COLUMN "users"."update_time" IS '更新时间';

-- 添加唯一约束
ALTER TABLE "users" ADD CONSTRAINT uk_username UNIQUE ("username");

-- ----------------------------
-- 问答历史表 (qa_history) (可选，用于功能扩展)
-- ----------------------------
DROP TABLE IF EXISTS "qa_history";
CREATE TABLE "qa_history" (
  "id" BIGSERIAL PRIMARY KEY,
  "user_id" BIGINT NOT NULL,
  "question" TEXT NOT NULL,
  "answer" TEXT,
  "create_time" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 添加注释
COMMENT ON TABLE "qa_history" IS '问答历史表';
COMMENT ON COLUMN "qa_history"."id" IS '主键ID';
COMMENT ON COLUMN "qa_history"."user_id" IS '用户ID';
COMMENT ON COLUMN "qa_history"."question" IS '用户提出的问题';
COMMENT ON COLUMN "qa_history"."answer" IS 'AI返回的回答';
COMMENT ON COLUMN "qa_history"."create_time" IS '创建时间';

-- 添加索引
CREATE INDEX idx_user_id ON "qa_history" ("user_id");

-- 插入一些测试数据 (可选)
INSERT INTO "user" ("username", "password") VALUES ('testuser', '$2a$10$abcdefghijklmnopqrstuv'); -- 密码是加密的，请通过注册接口创建用户