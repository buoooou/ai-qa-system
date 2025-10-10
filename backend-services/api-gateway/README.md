# AI QA System - API 网关

## 项目简介
基于 Spring Cloud Gateway 的 API 网关，负责路由和负载均衡。

## 功能列表
- 请求路由
- 限流与鉴权
- Swagger UI 集成

## 技术栈
- Spring Boot 3
- Spring Cloud Gateway
- Nacos

## 环境要求
- Java 17+
- Maven 3.8+

## 安装与运行
1. 克隆仓库
2. 运行 `mvn clean install`
3. 启动服务：`java -jar target/api-gateway.jar`

## 配置说明
- 修改 `application.yml` 配置 Nacos 地址

## 常见问题
- 确保 Nacos 服务已启动。