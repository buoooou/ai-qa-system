# AI问答系统

一个基于微服务架构的AI问答系统，采用Spring Cloud生态系统构建，包含API网关、用户服务和问答服务三个核心模块。

## 项目架构

```
ai-qa-system/
├── backend-services/
│   ├── api-gateway/        # API网关服务
│   ├── user-service/       # 用户管理服务
│   └── qa-service/         # 问答核心服务
└── ...
```

## 服务模块介绍

### 1. API网关服务 (api-gateway)
- **端口**: 8080
- **功能**: 作为整个系统的统一入口，负责请求路由、负载均衡、JWT认证
- **核心技术**: Spring Cloud Gateway + Nacos服务发现 + JWT
- **主要职责**:
  - 接收所有外部请求
  - 将`/api/user/**`路径的请求路由到用户服务
  - 将`/api/qa/**`路径的请求路由到问答服务
  - 实现JWT认证和Token验证
  - 服务发现和负载均衡

### 2. 用户服务 (user-service)
- **端口**: 8081
- **功能**: 负责用户管理相关功能，包括用户认证、注册、信息维护等
- **核心技术**: Spring Boot Web + Spring Data JPA + MySQL + Spring Security
- **主要职责**:
  - 提供用户登录、注册等接口
  - 管理用户基本信息和状态
  - 通过JPA操作MySQL数据库存储用户数据
  - 提供用户信息查询接口供其他服务调用

### 3. 问答服务 (qa-service)
- **端口**: 8082
- **功能**: 负责问答系统核心功能，处理用户提问、答案检索等业务
- **核心技术**: Spring Boot Web + OpenFeign + Nacos服务发现 + JPA + MySQL
- **主要职责**:
  - 提供问答相关接口
  - 通过OpenFeign调用用户服务获取用户信息
  - 存储问答历史记录到MySQL数据库

## 服务间调用关系

```
外部客户端
    ↓ (HTTP请求)
API网关服务 (端口8080)
    ↓ (路由转发)
用户服务 (端口8081) ←→ MySQL数据库
    ↓ (Feign调用)
问答服务 (端口8082) ←→ MySQL数据库
```

### 请求流程
1. 所有外部请求首先到达API网关服务(端口8080)
2. 网关根据JWT认证验证请求合法性
3. 网关根据请求路径将请求路由到相应的服务：
   - `/api/user/**`路径的请求转发到用户服务(端口8081)
   - `/api/qa/**`路径的请求转发到问答服务(端口8082)

### 服务间调用
- 问答服务通过OpenFeign调用用户服务获取用户信息
- 用户服务和问答服务都直接操作MySQL数据库进行数据持久化

### 服务注册与发现
- 所有服务(包括网关)都注册到Nacos服务注册中心
- 网关通过Nacos发现用户服务和问答服务实例，实现动态路由和负载均衡

## 技术栈

- **核心框架**: Spring Boot 2.7.17, Spring Cloud 2021.0.8
- **服务发现**: Nacos
- **服务网关**: Spring Cloud Gateway
- **服务调用**: OpenFeign
- **数据持久化**: Spring Data JPA, MySQL 8.0
- **安全认证**: JWT
- **API文档**: SpringDoc OpenAPI (Swagger)
- **项目构建**: Maven
- **其他**: Lombok

## 数据库设计

系统使用MySQL数据库，包含以下主要表结构：

### 用户表 (user)
```sql
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
```

### 问答历史表 (qa_history)
```sql
CREATE TABLE `qa_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `question` TEXT NOT NULL COMMENT '用户提出的问题',
  `answer` LONGTEXT COMMENT 'AI返回的回答',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问答历史表';
```

## 环境要求

- JDK 17
- Maven 3.6+
- MySQL 8.0+
- Nacos 2.0+

## 配置说明

所有服务都支持开发环境和生产环境配置：

- 开发环境配置: `application.yml`
- 生产环境配置: `application-prod.yml`

通过`--spring.profiles.active=prod`参数激活生产环境配置。

## 部署步骤

1. 启动Nacos服务注册中心
2. 创建MySQL数据库并执行初始化脚本
3. 修改各服务的配置文件中的数据库连接信息和Nacos地址
4. 依次启动各服务:
   - API网关服务: `mvn spring-boot:run` (端口8080)
   - 用户服务: `mvn spring-boot:run` (端口8081)
   - 问答服务: `mvn spring-boot:run` (端口8082)

## API接口

### 用户服务接口
- `POST /api/users/login` - 用户登录
- `POST /api/users/register` - 用户注册
- `PUT /api/users/updateNick` - 更新用户昵称
- `GET /api/users/getUserById` - 根据ID获取用户信息

### 问答服务接口
- `POST /api/qa/ask` - 提出问题
- `GET /api/qa/history` - 获取问答历史
- `GET /api/qa/{id}` - 根据ID获取特定问答记录

## 安全机制

系统采用JWT Token进行身份验证：
1. 用户通过登录接口获取Token
2. 后续请求需在Header中携带Token: `Authorization: Bearer <token>`
3. API网关负责Token验证和权限控制

## 开发规范

项目遵循以下开发规范：
- 使用Lombok简化Java Bean代码
- 使用SpringDoc OpenAPI生成API文档
- 统一的响应格式
- 全局异常处理机制
- 参数校验机制

## 扩展性设计

系统具备良好的扩展性：
- 微服务架构支持独立部署和扩展
- Feign客户端便于新增服务间调用
- Nacos支持动态服务发现和配置管理
- 可方便集成真实的AI问答引擎替换模拟实现