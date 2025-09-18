# AI QA System - 问答微服务

## 项目简介
处理问答逻辑的微服务，提供问题存储和回答生成功能。

## 功能列表
- 问题存储
- 回答生成
- 历史记录查询

## 技术栈
- Spring Boot 3
- Spring Data Redis
- Redis

## 环境要求
- Java 17+
- Redis 6+

## 安装与运行
1. 克隆仓库
2. 运行 `mvn clean install`
3. 启动服务：`java -jar target/qa-service.jar`

## 配置说明
- 修改 `application.yml` 配置 Redis 连接

## 常见问题
- 确保 Redis 服务已启动。