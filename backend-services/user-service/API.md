# User Service API 文档

## 项目概述

用户服务模块提供用户管理相关的API接口，包括用户注册、登录、信息查询和昵称更新等功能。

## 技术栈

- **Spring Boot**: 2.7.17
- **Spring Cloud**: 2021.0.8
- **Spring Cloud Alibaba**: 2021.0.5.0
- **Spring Data JPA**: 数据访问层
- **MySQL**: 数据库
- **Nacos**: 服务注册与发现
- **Lombok**: 代码简化

## API 接口

### 1. 用户注册

**POST** `/api/users/register`

请求体：
```json
{
    "username": "testuser",
    "password": "password123",
    "nickname": "测试用户"
}
```

响应：
```json
{
    "code": 200,
    "message": "Success",
    "data": {
        "id": 1,
        "userName": "testuser",
        "nickname": "测试用户",
        "createTime": "2023-01-01T10:00:00",
        "updateTime": "2023-01-01T10:00:00"
    }
}
```

### 2. 用户登录

**POST** `/api/users/login`

请求体：
```json
{
    "username": "testuser",
    "password": "password123"
}
```

响应：
```json
{
    "code": 200,
    "message": "Success",
    "data": {
        "id": 1,
        "userName": "testuser",
        "nickname": "测试用户",
        "createTime": "2023-01-01T10:00:00",
        "updateTime": "2023-01-01T10:00:00"
    }
}
```

### 3. 更新用户昵称

**PUT** `/api/users/{userId}/nickname`

请求体：
```json
{
    "nickname": "新昵称"
}
```

响应：
```json
{
    "code": 200,
    "message": "Success",
    "data": {
        "id": 1,
        "userName": "testuser",
        "nickname": "新昵称",
        "createTime": "2023-01-01T10:00:00",
        "updateTime": "2023-01-01T11:00:00"
    }
}
```

### 4. 根据用户ID查询用户

**GET** `/api/users/{userId}`

响应：
```json
{
    "code": 200,
    "message": "Success",
    "data": {
        "id": 1,
        "userName": "testuser",
        "nickname": "测试用户",
        "createTime": "2023-01-01T10:00:00",
        "updateTime": "2023-01-01T10:00:00"
    }
}
```

### 5. 根据用户名查询用户

**GET** `/api/users/username/{username}`

响应：
```json
{
    "code": 200,
    "message": "Success",
    "data": {
        "id": 1,
        "userName": "testuser",
        "nickname": "测试用户",
        "createTime": "2023-01-01T10:00:00",
        "updateTime": "2023-01-01T10:00:00"
    }
}
```

## 错误响应格式

```json
{
    "code": 500,
    "message": "错误信息",
    "data": null
}
```

## 数据库表结构

### users 表

| 字段名 | 类型 | 描述 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | AUTO_INCREMENT, PRIMARY KEY |
| user_name | VARCHAR(50) | 用户名 | NOT NULL, UNIQUE |
| password | VARCHAR(100) | 密码 | NOT NULL |
| nickname | VARCHAR(50) | 昵称 | NULL |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

## 启动配置

### 应用配置 (application.yml)

```yaml
server:
  port: 8081

spring:
  application:
    name: user-service-1
  cloud:
    nacos:
      discovery:
        server-addr: 54.219.180.170:8848
  datasource:
    url: jdbc:mysql://54.219.180.170:3306/ai_qa_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: ai_qa_system
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
    show-sql: true
```

## 项目结构

```
src/main/java/com/ai/qa/user/
├── api/
│   ├── controller/
│   │   └── UserController.java        # REST控制器
│   └── dto/                           # 数据传输对象
│       ├── Response.java              # 统一响应格式
│       ├── UserLoginRequest.java      # 登录请求DTO
│       ├── UserRegisterRequest.java   # 注册请求DTO
│       ├── UserResponse.java          # 用户响应DTO
│       └── UpdateNicknameRequest.java # 更新昵称请求DTO
├── application/
│   ├── UserService.java              # 业务接口
│   └── impl/
│       └── UserServiceImpl.java      # 业务实现
├── domain/
│   └── entity/
│       └── User.java                 # 用户实体
├── infrastructure/
│   ├── config/                       # 配置类
│   │   ├── SecurityConfig.java
│   │   └── SwaggerConfig.java
│   └── repository/
│       └── UserRepository.java       # 数据访问接口
└── UserServiceApplication.java       # 主启动类
```