# 用户服务 (User Service)

用户服务是 AI-QA 系统的核心微服务之一，负责用户注册、登录、个人信息管理等功能。

## 🚀 功能特性

- **用户注册**：支持新用户注册，包含用户名唯一性验证
- **用户登录**：支持用户名密码登录，返回 JWT Token
- **个人信息管理**：支持更新用户昵称
- **权限验证**：基于 JWT Token 的权限控制
- **API 文档**：自动生成 Swagger/OpenAPI 文档
- **数据持久化**：使用 JPA + MySQL 存储用户数据

## 📋 API 接口

### 1. 用户注册
```http
POST /api/users/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

### 2. 用户登录
```http
POST /api/users/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "nickName": "测试用户",
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400
  }
}
```

### 3. 更新昵称
```http
PUT /api/users/nick
Authorization: Bearer <JWT-Token>
Content-Type: application/json

{
  "userId": 1,
  "nickName": "新昵称"
}
```

## 🛠️ 技术栈

- **框架**：Spring Boot 2.7.x
- **数据库**：MySQL 8.0
- **ORM**：Spring Data JPA
- **安全**：Spring Security + JWT
- **密码加密**：BCrypt
- **API 文档**：SpringDoc OpenAPI 3.0
- **构建工具**：Maven

## 📁 项目结构

```
user-service/
├── src/main/java/com/ai/qa/user/
│   ├── UserServiceApplication.java      # 启动类
│   ├── api/
│   │   ├── controller/
│   │   │   └── UserController.java      # REST控制器
│   │   └── dto/
│   │       ├── UserLoginRequest.java    # 登录请求DTO
│   │       ├── UserRegisterRequest.java # 注册请求DTO
│   │       ├── UpdateNickRequest.java   # 更新昵称DTO
│   │       └── LoginResponse.java       # 登录响应DTO
│   ├── application/
│   │   ├── userService.java            # 服务接口
│   │   └── impl/
│   │       └── userServiceImpl.java    # 服务实现
│   ├── domain/
│   │   └── entity/
│   │       └── User.java               # 用户实体
│   ├── infrastructure/
│   │   └── repository/
│   │       └── UserRepository.java     # 数据访问层
│   └── security/
│       ├── SecurityConfig.java         # 安全配置
│       └── JwtUtil.java               # JWT工具类
├── src/main/resources/
│   └── application.yml                # 配置文件
└── pom.xml                            # Maven配置
```

## 🔧 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_qa_user?useSSL=false&serverTimezone=UTC
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
```

### JWT 配置
```yaml
jwt:
  secret: ai-qa-system-secret-key-for-jwt-token-generation
  expiration: 86400000  # 24小时，单位毫秒
```

### Swagger 文档
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **API Docs**: http://localhost:8081/api-docs

## 🚀 快速开始

### 1. 环境要求
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库准备
```sql
CREATE DATABASE ai_qa_user CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置修改
编辑 `src/main/resources/application.yml`：
- 修改数据库连接信息
- 修改JWT密钥（生产环境务必更换）

### 4. 启动服务
```bash
# 开发环境启动
mvn spring-boot:run

# 或使用IDE运行 UserServiceApplication.java
```

### 5. 验证服务
- 访问 Swagger UI: http://localhost:8081/swagger-ui.html
- 测试注册接口
- 测试登录接口获取 JWT Token

## 🔍 测试示例

### 使用 curl 测试
```bash
# 注册新用户
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'

# 用户登录
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'

# 更新昵称（替换<token>为实际JWT）
curl -X PUT http://localhost:8081/api/users/nick \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"nickName":"新昵称"}'
```

## 📝 注意事项

1. **安全配置**：生产环境请更换 JWT 密钥
2. **密码安全**：所有密码使用 BCrypt 加密存储
3. **用户唯一性**：用户名必须唯一
4. **权限控制**：用户只能修改自己的信息
5. **时间处理**：创建时间和更新时间由 JPA 自动管理

## 🔐 安全特性

- **密码加密**：使用 BCrypt 算法加密存储
- **JWT 认证**：无状态认证，支持分布式部署
- **权限验证**：基于用户ID的权限控制
- **输入验证**：使用 Bean Validation 进行参数校验

## 📞 问题反馈

如有问题或建议，请通过以下方式联系：
- 提交 Issue
- 发送邮件至开发团队

## 🔄 版本历史

- **v1.0.0**: 基础用户服务，包含注册、登录、昵称更新功能