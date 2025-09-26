# AI QA 系统 - 后端服务工程

## 工程概述
本工程是 AI QA 系统的后端服务部分，采用微服务架构，包含 API 网关、问答服务和用户服务三大模块。通过 RESTful API 与前端交互，支持用户认证、问答会话管理等功能。

## 技术栈
- **框架**: Spring Boot 3.x
- **数据库**: MySQL (主库) + Redis (缓存)
- **认证**: JWT (JSON Web Token)
- **API 文档**: Swagger/OpenAPI
- **构建工具**: Maven
- **测试框架**: JUnit 5 + Mockito

## 目录结构
```
backend-services/
├── api-gateway/               # API 网关模块
│   ├── src/main/java/com/example/apigateway/  # 网关核心代码
│   ├── src/main/resources/application.yml     # 网关配置
├── qa-service/               # 问答服务模块
│   ├── src/main/java/com/ai/qa/service/       # 问答业务逻辑
│   ├── src/main/resources/application.yml     # 服务配置
├── user-service/             # 用户服务模块
│   ├── src/main/java/com/ai/qa/user/         # 用户认证与管理
│   ├── src/main/resources/application.yml     # 服务配置
├── pom.xml                   # 父工程 Maven 配置
```

## 模块分析
### 1. API 网关模块 (`api-gateway`)
- **功能**: 统一入口、路由转发、限流熔断。
- **核心类**:
  - `ApiGatewayApplication`: 网关启动类
  - `InMemoryRateLimiterConfig`: 限流配置

### 2. 问答服务模块 (`qa-service`)
- **功能**: 处理问答会话、消息存储、历史记录查询。
- **核心类**:
  - `QAController`: 问答接口
  - `GeminiService`: AI 问答服务
  - `QAHistoryService`: 历史记录管理

### 3. 用户服务模块 (`user-service`)
- **功能**: 用户注册/登录、JWT 签发、权限管理。
- **核心类**:
  - `UserController`: 用户接口
  - `AuthService`: 认证服务
  - `JwtUtil`: JWT 工具

## 部署与运行
### 本地开发
1. 安装依赖:
   ```bash
   mvn clean install
   ```
2. 启动服务 (按需启动模块):
   ```bash
   cd api-gateway && mvn spring-boot:run
   cd qa-service && mvn spring-boot:run
   cd user-service && mvn spring-boot:run
   ```
3. 访问 Swagger:
   - 网关: `http://localhost:8080/swagger-ui.html`
   - 各服务: 端口见对应 `application.yml`

### 生产部署
1. 打包各模块:
   ```bash
   mvn package -DskipTests
   ```
2. 运行:
   ```bash
   java -jar api-gateway/target/*.jar
   java -jar qa-service/target/*.jar
   java -jar user-service/target/*.jar
   ```

## 注意事项
1. **服务发现**: 当前为直连模式，生产环境建议接入注册中心
2. **配置管理**: 敏感配置应通过环境变量注入
3. **跨域处理**: 网关需配置前端域名白名单

## 后续优化
1. 引入 Spring Cloud 服务治理
2. 配置中心统一管理
3. 链路追踪集成

---
维护团队: engineering@example.com