# AI智能问答客服系统

## 项目概述

这是一个基于微服务架构的AI智能问答客服系统，采用DDD（领域驱动设计）架构模式，使用Spring Cloud + Next.js技术栈，集成Google Gemini AI API，提供智能问答服务。

## 系统架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   API Gateway   │    │  Microservices  │
│   (Next.js)     │◄──►│ (Spring Cloud   │◄──►│                 │
│   Port: 3000    │    │  Gateway)       │    │  User Service   │
└─────────────────┘    │  Port: 8080     │    │  Port: 8081     │
                       └─────────────────┘    │                 │
                                              │  QA Service     │
                                              │  Port: 8082     │
                                              └─────────────────┘
                                                       │
                                              ┌─────────────────┐
                                              │   External APIs │
                                              │                 │
                                              │  Gemini AI API  │
                                              │  MySQL Database │
                                              │  Nacos Registry │
                                              └─────────────────┘
```

## 技术栈

### 后端技术
- **Spring Boot 2.7.17** - 微服务框架
- **Spring Cloud Gateway** - API网关
- **Spring Cloud Alibaba Nacos** - 服务注册与发现
- **Spring Data JPA** - 数据访问层
- **Spring Security** - 安全认证框架
- **MySQL 8.0** - 关系型数据库
- **Lombok** - 代码简化工具
- **Maven** - 项目构建工具
- **Docker** - 容器化部署

### 前端技术（计划）
- **Next.js** - React全栈框架
- **React Hooks** - 状态管理
- **Axios** - HTTP客户端
- **Tailwind CSS** - 样式框架

### AI集成
- **Google Gemini API** - 智能问答服务
- **RestTemplate/WebClient** - HTTP客户端

## 项目结构

```
ai-qa-system/
├── backend-services/           # 后端微服务
│   ├── pom.xml                # 父级Maven配置
│   ├── api-gateway/           # API网关服务
│   │   ├── src/main/java/com/ai/qa/gateway/
│   │   │   ├── ApiGatewayApplication.java
│   │   │   ├── config/GatewayConfig.java
│   │   │   ├── filter/LoggingFilter.java
│   │   │   └── controller/GatewayController.java
│   │   ├── src/main/resources/
│   │   │   └── application.yml
│   │   └── pom.xml
│   ├── user-service/          # 用户管理服务（DDD架构）
│   │   ├── src/main/java/com/ai/qa/user/
│   │   │   ├── domain/entity/     # 领域实体
│   │   │   ├── infrastructure/    # 基础设施层
│   │   │   │   ├── repository/    # 数据访问层
│   │   │   │   └── config/        # 配置类
│   │   │   ├── application/       # 应用服务层
│   │   │   ├── api/              # 接口层
│   │   │   │   ├── controller/    # 控制器
│   │   │   │   ├── dto/          # 响应对象
│   │   │   │   └── exception/     # 异常处理
│   │   │   ├── dto/              # 数据传输对象
│   │   │   ├── common/           # 通用工具类
│   │   │   └── UserServiceApplication.java
│   │   ├── src/main/resources/
│   │   │   ├── application.yml
│   │   │   └── sql/init.sql
│   │   └── pom.xml
│   └── qa-service/            # 问答服务
│       ├── src/main/java/com/ai/qa/service/
│       │   ├── entity/        # 实体类
│       │   ├── repository/    # 数据访问层
│       │   ├── service/       # 业务逻辑层
│       │   ├── controller/    # 控制器层
│       │   ├── dto/          # 数据传输对象
│       │   ├── client/       # 外部API客户端
│       │   └── QaServiceApplication.java
│       ├── src/main/resources/
│       │   └── application.yml
│       └── pom.xml
├── frontend/                  # 前端项目（待创建）
├── docker-compose.yml         # Docker编排文件
└── README.md                 # 项目说明文档
```

## 核心功能

### 用户管理服务 (User Service) - DDD架构
- ✅ 用户注册和登录（完整业务逻辑）
- ✅ 密码加密存储（BCrypt）
- ✅ 用户信息管理（CRUD操作）
- ✅ 用户状态管理（启用/禁用）
- ✅ 数据验证和异常处理
- ✅ 面向接口编程（IUserService接口）
- ✅ DDD分层架构（领域层、应用层、基础设施层、接口层）
- ✅ 统一响应格式
- ✅ 完整的错误码体系
- ✅ Swagger API文档支持

### 问答服务 (QA Service)
- ✅ AI智能问答（集成Gemini API）
- ✅ 问答历史记录
- ✅ 上下文对话支持
- ✅ 问答记录搜索
- ✅ 分页查询支持
- ✅ 问答记录删除功能
- ✅ 面向接口编程（IQaService接口）
- ✅ 统一响应格式

### API网关 (API Gateway)
- ✅ 统一入口和路由
- ✅ 服务发现和负载均衡
- ✅ CORS跨域支持
- ✅ 请求日志记录
- ✅ 全局过滤器

## API接口文档

### 用户服务接口 (User Service)
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/{userId}` - 根据ID查询用户
- `GET /api/user/username/{username}` - 根据用户名查询用户
- `PUT /api/user/{userId}` - 更新用户信息
- `PUT /api/user/{userId}/password` - 修改密码
- `PUT /api/user/{userId}/disable` - 禁用用户
- `PUT /api/user/{userId}/enable` - 启用用户
- `GET /api/user/check/username/{username}` - 检查用户名是否存在
- `GET /api/user/check/email/{email}` - 检查邮箱是否存在
- `GET /api/user/health` - 健康检查
- `GET /api/user/info` - 获取服务信息

### 问答服务接口 (QA Service)
- `POST /api/qa/ask` - 提交问题获取AI回答
- `GET /api/qa/history/{userId}` - 获取用户问答历史
- `GET /api/qa/history/{userId}/paged` - 分页获取问答历史
- `GET /api/qa/search/{userId}` - 搜索问答历史
- `GET /api/qa/{id}` - 根据ID获取问答记录
- `GET /api/qa/count/{userId}` - 获取用户问答总数
- `DELETE /api/qa/{id}` - 删除问答记录
- `DELETE /api/qa/user/{userId}/all` - 批量删除用户问答记录
- `GET /api/qa/health` - 健康检查
- `GET /api/qa/info` - 获取服务信息

### API网关接口
- `GET /api/gateway/health` - 网关健康检查
- `GET /api/gateway/info` - 获取网关信息

## 环境配置

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_qa_system
    username: root
    password: your_password
```

### Nacos配置
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
```

### Gemini API配置
```yaml
gemini:
  api:
    base-url: https://generativelanguage.googleapis.com/v1beta
    key: YOUR_API_KEY_HERE  # 需要替换为实际的API Key
    model: gemini-pro
```

## 快速开始

### 1. 环境准备
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Docker & Docker Compose（可选）

### 2. 获取Gemini API Key
1. 访问 [Google AI Studio](https://makersuite.google.com/)
2. 使用Google账号登录
3. 创建新的API Key
4. 将API Key配置到 `qa-service/src/main/resources/application.yml`

### 3. 数据库初始化
执行 `user-service/src/main/resources/sql/init.sql` 中的SQL脚本

### 4. 启动服务

#### 方式一：本地启动
```bash
# 1. 编译所有服务
cd backend-services
mvn clean compile

# 2. 启动用户服务
cd user-service
mvn spring-boot:run

# 3. 启动问答服务（新终端）
cd ../qa-service
mvn spring-boot:run

# 4. 启动API网关（新终端）
cd ../api-gateway
mvn spring-boot:run
```

#### 方式二：Docker启动
```bash
# 构建并启动所有服务
docker-compose up --build

# 后台运行
docker-compose up -d --build
```

### 5. 验证服务
- API网关: http://localhost:8080/api/gateway/health
- 用户服务: http://localhost:8081/api/user/health
- 问答服务: http://localhost:8082/api/qa/health

## 测试示例

### 用户注册
```bash
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "confirmPassword": "password123",
    "email": "test@example.com"
  }'
```

### 用户登录
```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### AI问答
```bash
curl -X POST http://localhost:8080/api/qa/ask \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "question": "什么是人工智能？"
  }'
```

## 功能测试结果

### ✅ 已完成功能测试：

#### 1. API Gateway (端口8080)
* ✅ 健康检查正常 - `GET /actuator/health`
* ✅ 服务发现正常（发现了4个服务实例）
* ✅ 路由转发正常（/api/user/**, /api/auth/**, /api/qa/**）
* ✅ 负载均衡器工作正常（Spring Cloud LoadBalancer）
* ✅ 全局日志过滤器正常工作
* ✅ Actuator监控端点正常 - `GET /actuator/gateway/routes`
* ✅ CORS跨域配置正确

#### 2. User Service (端口8081)
* ✅ 用户注册功能正常 - `POST /api/user/register`
* ✅ JWT登录功能正常 - `POST /api/auth/login`（返回access_token和refresh_token）
* ✅ 令牌验证功能正常 - `POST /api/auth/verify`
* ✅ 令牌刷新功能正常 - `POST /api/auth/refresh`
* ✅ 用户信息查询功能正常 - `GET /api/auth/profile`
* ✅ 用户认证系统完整（JWT + Spring Security）
* ✅ Spring Security配置正确（允许认证端点访问）
* ✅ 密码加密存储（BCrypt）
* ✅ DDD架构实现完整

#### 3. QA Service (端口8082)
* ✅ AI问答功能正常 - `POST /api/qa/ask`（使用模拟回答，因为未配置真实Gemini API Key）
* ✅ 问答历史保存正常
* ✅ 问答历史查询正常 - `GET /api/qa/history/{userId}`
* ✅ 数据库集成正常（MySQL连接和数据持久化）
* ✅ 健康检查端点正常 - `GET /api/qa/health`
* ✅ 响应时间记录正常

#### 4. 微服务通信
* ✅ 通过API Gateway统一入口访问所有服务
* ✅ 服务发现和负载均衡正常（Nacos + Spring Cloud LoadBalancer）
* ✅ 服务间路由转发正常
* ✅ 负载均衡问题已修复（添加LoadBalancer依赖和配置）
* ✅ 跨域配置正确
* ✅ 错误处理和响应格式统一
* ✅ 全局日志记录正常

### 🔧 负载均衡修复详情：
1. **问题诊断**：API Gateway返回503 Service Unavailable错误
2. **解决方案**：
   - 添加`spring-cloud-starter-loadbalancer`依赖
   - 创建LoadBalancerConfig配置类
   - 添加全局日志过滤器用于调试
   - 优化application.yml负载均衡配置
3. **修复结果**：所有服务通过API Gateway访问正常

### 📊 测试数据示例：

#### 成功的API调用示例：
```bash
# Q&A服务测试
curl http://localhost:8080/api/qa/health
# 响应: {"code":200,"message":"QA Service is running","data":null}

# AI问答测试
curl -X POST http://localhost:8080/api/qa/ask \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"question":"通过API Gateway测试问答功能"}'
# 响应: {"code":200,"message":"问答成功","data":{"id":4,"userId":1,"question":"通过API Gateway测试问答功能","answer":"你好！我是AI智能助手，很高兴为您服务。请问有什么可以帮助您的吗？","createTime":"2025-09-08T13:45:04.464154","model":"gemini-pro","responseTime":219}}

# 用户认证测试
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"userName":"testuser","password":"password123"}'
# 响应: 正确的业务逻辑错误（401用户名或密码错误），证明路由正常
```

### 🏗️ 系统架构特点：
1. **微服务架构**：三个独立的Spring Boot服务，高内聚、低耦合
2. **统一网关**：Spring Cloud Gateway作为统一入口，提供路由、过滤、负载均衡
3. **服务发现**：使用Nacos进行服务注册和发现，支持动态扩缩容
4. **负载均衡**：Spring Cloud LoadBalancer实现服务负载均衡，支持多种策略
5. **JWT认证**：完整的用户认证和授权系统，支持令牌刷新和验证
6. **AI集成**：预留Gemini API集成接口，支持智能问答功能
7. **数据持久化**：MySQL数据库存储用户和问答历史，支持事务管理
8. **接口设计**：遵循RESTful API设计规范，统一响应格式
9. **监控运维**：集成Actuator提供健康检查和监控端点
10. **DDD架构**：用户服务采用领域驱动设计，分层清晰，易于维护

### 🔧 技术亮点：
- **Spring Cloud生态**：完整的微服务技术栈
- **容错机制**：服务间通信具备重试和熔断能力
- **配置管理**：支持多环境配置和动态配置更新
- **日志追踪**：全局请求日志记录，便于问题排查
- **安全防护**：JWT令牌认证，密码加密存储
- **扩展性强**：模块化设计，易于添加新功能

## 开发进度

### ✅ 第一阶段：微服务基础架构（已完成）
- [x] Maven多模块项目搭建
- [x] API Gateway服务完整实现
- [x] User Service完整实现（DDD架构）
- [x] QA Service完整实现
- [x] JWT认证系统完整实现
- [x] Gemini AI集成（模拟模式）
- [x] 数据库设计和配置
- [x] 统一响应格式
- [x] 异常处理机制
- [x] 代码编译通过
- [x] Lombok配置优化
- [x] 服务发现和注册（Nacos）
- [x] 负载均衡配置（Spring Cloud LoadBalancer）
- [x] 全面功能测试通过
- [x] 微服务间通信正常

### 🔄 第二阶段：前端开发（计划中）
- [ ] Next.js项目创建
- [ ] 用户界面设计
- [ ] 聊天界面实现
- [ ] API对接
- [ ] 响应式设计

### 📋 第三阶段：部署和优化（计划中）
- [ ] 获取真实Gemini API Key
- [ ] CI/CD流水线
- [ ] Docker容器化部署
- [ ] AWS部署
- [ ] Serverless探索
- [ ] 性能优化
- [ ] 监控和日志

## 架构特点

### DDD架构设计
- **领域层（Domain）**: 包含业务实体和领域逻辑
- **应用层（Application）**: 协调领域对象完成业务用例
- **基础设施层（Infrastructure）**: 提供技术实现，如数据库访问
- **接口层（API）**: 对外提供服务接口

### 设计模式
- **面向接口编程**: 所有服务层都定义了接口
- **依赖注入**: 使用Spring的IoC容器管理依赖
- **统一响应格式**: 所有API返回统一的Response格式
- **异常统一处理**: 完整的错误码体系和异常处理

## 注意事项

1. **API Key安全**: 请不要将Gemini API Key提交到版本控制系统
2. **数据库连接**: 确保数据库服务正常运行
3. **服务启动顺序**: 建议先启动用户服务和问答服务，再启动API网关
4. **端口冲突**: 确保8080、8081、8082端口未被占用
5. **Lombok配置**: 确保IDE已安装Lombok插件

## 故障排除

### 编译问题
- 确保JDK版本为17+
- 检查Maven配置是否正确
- 清理Maven缓存：`mvn clean`

### Lombok问题
- 确保IDE安装了Lombok插件
- 检查Maven编译插件配置
- 重新导入项目

### 数据库连接问题
- 检查数据库服务是否启动
- 验证连接参数是否正确
- 确认数据库用户权限

## 贡献指南

1. Fork本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 项目维护者: Qiao Zhe
- 邮箱: support@ai-qa-system.com
- 项目地址: https://github.com/your-username/ai-qa-system

---

**注意**: 这是一个教学项目，用于演示微服务架构、DDD设计模式和AI集成的最佳实践。
