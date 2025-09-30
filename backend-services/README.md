# AI问答系统后端服务

## 项目简介
这是一个基于Spring Cloud微服务架构的AI问答系统后端，集成Google Gemini API提供智能问答功能，同时包含完整的用户管理和认证授权机制。

## 系统架构
项目采用标准的微服务架构，由以下三个核心服务组成：

![系统架构图](https://placeholder-for-architecture-diagram)

### 1. API网关服务 (api-gateway)
- 端口：8080
- 主要职责：请求路由、负载均衡、CORS配置、统一入口管理

### 2. 用户服务 (user-service)
- 端口：8081
- 主要职责：用户管理、认证授权、JWT令牌生成与验证

### 3. 问答服务 (qa-service)
- 端口：8082
- 主要职责：AI问答处理、历史记录管理、调用Google Gemini API

## 技术栈

### 核心框架
- Spring Boot 2.7.17
- Spring Cloud 2021.0.8
- Spring Cloud Alibaba 2021.0.5.0

### 数据存储
- MySQL 8.0.33
- AWS RDS (远程数据库服务)
- Spring Data JPA

### 安全认证
- Spring Security
- JWT (JSON Web Token)

### 服务通信
- Spring Cloud Gateway
- OpenFeign

### 人工智能
- Google Gemini API (gemini-1.5-flash模型)

### 文档与工具
- SpringDoc OpenAPI (Swagger UI)
- Lombok
- Docker

## 快速开始

### 前提条件
- JDK 17或更高版本
- Maven 3.6+ 或 Gradle 7+
- Docker (可选，用于容器化部署)
- MySQL 8.0+ (或直接使用已配置的AWS RDS)

### 本地开发运行

1. **克隆项目**
```bash
git clone https://github.com/[your-username]/ai-qa-system.git
cd ai-qa-system/backend-services
```

2. **构建项目**
```bash
mvn clean install
```

3. **启动各服务**
可以按照以下顺序启动各服务：
- 首先启动user-service
- 然后启动qa-service
- 最后启动api-gateway

在各服务目录下执行：
```bash
mvn spring-boot:run
```

### Docker容器化运行

1. **构建Docker镜像**
在项目根目录执行：
```bash
# 构建用户服务镜像
cd user-service
docker build -t ai-qa-user-service .

# 构建问答服务镜像
cd ../qa-service
docker build -t ai-qa-qa-service .

# 构建API网关镜像
cd ../api-gateway
docker build -t ai-qa-api-gateway .
```

2. **运行Docker容器**
```bash
docker run -p 8081:8081 ai-qa-user-service
docker run -p 8082:8082 ai-qa-qa-service
docker run -p 8080:8080 ai-qa-api-gateway
```

## 配置说明

### 数据库配置
所有服务共享同一个MySQL数据库，配置在各服务的`application.yml`文件中：
```yaml
spring:
  datasource:
    url: jdbc:mysql://liug-aiqasystemdb.cjkwkoiq2pgc.us-east-2.rds.amazonaws.com/ai_qa_system?useUnicode=true&characterEncoding=utf-8
    username: admin
    password: zaq12wsx
```

### JWT配置 (user-service)
```yaml
jwt:
  secret: 63ffbc2b8d13ad5180ed7ae7c67f18c85d86046732fc9ced6a02a9d50abb1a03 # 建议替换为自己的密钥
  expiration:
    ms: 86400000 # token过期时间(毫秒)，默认为24小时
```

### Gemini API配置 (qa-service)
```yaml
gemini:
  api:
    base-url: https://generativelanguage.googleapis.com/v1beta
    key: AIzaSyB5nVaGOcJYSsxwmPVe1kVYZxY2Dl504Zg
    model: gemini-1.5-flash  # 免费模型，速度快，适合对话
```

## API文档
系统集成了Swagger UI，可通过以下地址访问API文档：

- API网关文档入口：http://localhost:8080/swagger-ui.html
- 用户服务文档：http://localhost:8081/swagger-ui.html
- 问答服务文档：http://localhost:8082/swagger-ui.html

## 核心功能

### 用户服务
- **用户注册**：创建新用户账号，密码加密存储
- **用户登录**：验证用户凭据并生成JWT令牌
- **个人信息管理**：更新用户昵称和密码
- **用户信息查询**：根据用户ID查询用户信息

### 问答服务
- **智能问答**：集成Google Gemini API提供AI回答
- **问答历史**：保存和查询用户的问答历史记录
- **健康检查**：提供服务运行状态检查接口

### API网关
- **请求路由**：将请求转发到相应的微服务
- **跨域配置**：支持与前端应用的跨域通信
- **负载均衡**：支持服务实例的负载均衡

## 服务间通信
系统使用OpenFeign进行服务间通信：
- qa-service通过UserClient调用user-service的接口
- 所有外部请求通过api-gateway统一转发到相应服务

## 安全机制
- 基于JWT的无状态认证
- Spring Security保护API接口
- 密码加密存储
- 细粒度的访问控制

## 监控与维护
各服务均提供健康检查接口：
- 用户服务：http://localhost:8081/api/user/health
- 问答服务：http://localhost:8082/api/qa/health
- API网关：通过Actuator端点监控

## 部署说明

### 开发环境
按照「快速开始」章节的说明启动各服务即可。

### 生产环境
建议使用Docker容器化部署，并结合Kubernetes进行容器编排管理。

## 常见问题与解决方案

1. **数据库连接失败**
   - 检查AWS RDS实例是否可访问
   - 验证数据库连接参数是否正确

2. **Gemini API调用失败**
   - 检查API密钥是否有效
   - 确认网络代理配置正确
   - 查看应用日志了解具体错误信息

3. **服务间通信异常**
   - 检查服务是否已启动
   - 验证Feign客户端配置
   - 查看网关路由配置是否正确

## 参与开发

### 代码结构规范
- 采用领域驱动设计(DDD)思想组织代码
- 分层架构：API层、Application层、Domain层、Infrastructure层

### 开发流程
1. Fork项目仓库
2. 创建特性分支
3. 提交代码并推送到远程仓库
4. 创建Pull Request

## 版权信息
MIT License

## 联系我们
如有任何问题或建议，请联系项目维护者。