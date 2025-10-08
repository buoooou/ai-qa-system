CREATE TABLE qa_history (
id VARCHAR(64) PRIMARY KEY, -- QA 历史记录唯一 ID
user_id VARCHAR(64) NOT NULL, -- 用户 ID
question TEXT NOT NULL, -- 用户问题
answer TEXT, -- AI 回答
session_id VARCHAR(64), -- 会话 ID，可选
rag_answer TEXT, -- RAG 增强答案，可选
timestamp DATETIME NOT NULL, -- 问答时间
create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

Nacos：
qa-service-zjy:

google:
ai:
api-key: AIzaSyCGvYQa-5kJDbbXsr372wHsHBWEDtufjBM
model: gemini-1.5-flash
base-url: https://generativelanguage.googleapis.com/v1beta

spring:
datasource:
url: jdbc:mysql://localhost:3306/ai_qa_system?useSSL=false&serverTimezone=UTC
username: root
password: QWE123asd
driver-class-name: com.mysql.cj.jdbc.Driver
jpa:
hibernate:
ddl-auto: validate
show-sql: true
