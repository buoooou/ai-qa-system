# AI问答系统 (AI-QA-System)

## 项目简介

AI问答系统是一个基于Spring Cloud微服务架构的智能问答平台，旨在提供高效、可扩展的问答服务，支持用户身份认证和个性化问答处理。

## 项目结构

该项目采用微服务架构设计，由以下三个核心服务组成：

```
backend-services/
├── api-gateway/       # API网关服务，统一入口
├── user-service/      # 用户服务，负责用户认证和管理
├── qa-service/        # 问答服务，处理核心问答逻辑
└── pom.xml            # 父项目依赖管理
```

## 技术栈

| 类别 | 技术/框架 | 版本 | 用途 |
|------|----------|------|------|
| 开发语言 | Java | 17 | 系统开发语言 |
| 基础框架 | Spring Boot | 3.1.5 | 微服务基础框架 |
| 微服务组件 | Spring Cloud | 2022.0.5 | 微服务生态组件 |
| 微服务组件 | Spring Cloud Alibaba | 2022.0.0.0 | 阿里微服务解决方案 |
| 服务注册/配置 | Nacos | - | 服务注册与配置中心 |
| API网关 | Spring Cloud Gateway | - | 统一入口、路由转发 |
| 服务通信 | OpenFeign | - | 声明式REST客户端 |
| 数据存储 | MySQL | 8.0.33 | 用户数据持久化 |
| ORM框架 | Spring Data JPA | - | 数据库访问层框架 |
| 工具库 | Lombok | - | 简化Java代码 |
| 认证 | JWT | 0.11.5 | 用户身份认证 |

## 核心服务说明

### 1. API网关 (api-gateway)

**功能定位**：系统的统一入口，负责请求路由、负载均衡等功能。

**关键特性**：
- 运行在8080端口，是所有后端请求的唯一入口
- 基于Spring Cloud Gateway实现路由转发
- 配置了两条核心路由规则：
  - `/api/user/**` → user-service服务
  - `/api/qa/**` → qa-service服务
- 集成Nacos服务发现，支持动态路由和负载均衡
- 包含JWT认证相关依赖，预留了认证功能扩展

### 2. 用户服务 (user-service)

**功能定位**：负责用户认证、用户信息管理等核心功能。

**关键特性**：
- 运行在8081端口
- 提供用户认证和用户信息查询API
- 集成MySQL数据库存储用户数据
- 采用Spring Data JPA进行ORM操作
- 已实现的API端点：
  - `POST /api/user/login`：用户登录接口
  - `GET /api/user/{userId}`：根据ID查询用户信息

### 3. 问答服务 (qa-service)

**功能定位**：处理用户的问答请求，是系统的核心业务服务。

**关键特性**：
- 运行在8082端口
- 通过OpenFeign调用user-service获取用户信息
- 实现了问答处理的基础框架
- 已实现的API端点：
  - `GET /api/qa/test`：测试问答服务功能

## 服务间交互流程

1. 客户端请求首先经过API网关（8080端口）
2. API网关根据请求路径将请求路由到相应的微服务：
   - `/api/user/**` 请求路由到user-service（8081端口）
   - `/api/qa/**` 请求路由到qa-service（8082端口）
3. qa-service在处理请求过程中，通过OpenFeign调用user-service获取用户信息
4. user-service从MySQL数据库中查询用户数据并返回给qa-service
5. 最终结果通过API网关返回给客户端

## 快速开始

### 前提条件

- JDK 17或更高版本
- Maven 3.6+ 
- MySQL 8.0+ 
- Nacos服务（项目中配置的是远程Nacos服务：54.219.180.170:8848）

### 构建项目

在项目根目录下执行以下命令构建整个项目：

```bash
mvn clean install
```

### 运行服务

分别启动三个服务：

1. 启动API网关服务：
```bash
cd backend-services/api-gateway
mvn spring-boot:run
```

2. 启动用户服务：
```bash
cd backend-services/user-service
mvn spring-boot:run
```

3. 启动问答服务：
```bash
cd backend-services/qa-service
mvn spring-boot:run
```

## API文档

### 用户服务API

#### 用户登录
- **URL**: `/api/user/login`
- **Method**: `POST`
- **Request Body**: 
```json
{
  "username": "string",
  "password": "string"
}
```
- **Response**: 
```json
{
  "token": "string"
}
```

#### 根据ID查询用户信息
- **URL**: `/api/user/{userId}`
- **Method**: `GET`
- **Path Param**: `userId` - 用户ID
- **Response**: `string` - 用户信息

### 问答服务API

#### 测试问答服务
- **URL**: `/api/qa/test`
- **Method**: `GET`
- **Response**: `string` - 测试结果

## 配置说明

### 数据库配置（user-service）

```yaml
spring:
  datasource:
    url: jdbc:mysql://54.219.180.170:3306/ai_qa_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: ai_qa_system
```

### Nacos配置

所有服务均配置了Nacos服务注册中心：

```yaml
spring:
  cloud:
    nacos:
      server-addr: 54.219.180.170:8848
```

## 项目架构特点

1. **模块化设计**：清晰分离了API网关、用户服务和问答服务三个核心模块
2. **分层架构**：每个服务内部采用了API层、应用层、领域层和基础设施层的分层设计
3. **微服务最佳实践**：
   - 服务注册与发现（Nacos）
   - 统一API网关（Spring Cloud Gateway）
   - 声明式服务调用（OpenFeign）
   - 负载均衡（Spring Cloud LoadBalancer）
4. **零信任安全设计**：即使在微服务内部也考虑了安全验证机制
5. **DTO模式**：使用数据传输对象进行服务间数据传递

## 未来扩展方向

1. 完善用户服务功能，实现完整的用户管理系统
2. 增强问答服务的AI能力，集成实际的问答模型
3. 实现完整的JWT认证流程
4. 添加日志、监控和熔断机制
5. 完善API文档和错误处理机制
6. 增加前端界面实现完整的用户交互体验
