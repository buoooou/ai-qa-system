# AI Q&A System

一个基于微服务架构的AI问答系统，采用Spring Cloud和NextJS构建。

## 系统架构

### 后端微服务
- **API Gateway** (8080): 统一入口，路由分发
- **User Service** (8081): 用户管理、JWT认证
- **QA Service** (8082): 问答处理、AI模型集成

### 前端
- **NextJS Frontend** (3000): 用户界面

### 基础设施
- **MySQL** (3306): 数据存储
- **Nacos** (8848): 服务发现与配置管理

## 技术栈

### 后端
- Java 17
- Spring Boot 2.7.17
- Spring Cloud 2021.0.8
- Spring Cloud Alibaba 2021.0.5.0
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0
- Nacos

### 前端
- NextJS 14
- React 18
- TypeScript
- Tailwind CSS
- Axios

## 快速启动

### 方式一：Docker Compose（推荐）

1. 克隆项目
```bash
git clone <repository-url>
cd ai-qa-system
```

2. 设置环境变量（可选）
```bash
export AI_API_KEY=your-openai-api-key
```

3. 启动所有服务
```bash
docker-compose up -d
```

4. 等待服务启动（约2-3分钟）
```bash
# 检查服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f
```

5. 访问应用
- 前端界面: http://localhost:3000
- API Gateway: http://localhost:8080
- Nacos控制台: http://localhost:8848/nacos

### 方式二：本地开发模式

#### 前置条件
- JDK 17
- Node.js 18+
- MySQL 8.0
- Nacos Server

#### 后端服务

1. 构建项目
```bash
cd backend-services
mvn clean package -DskipTests
```

2. 启动服务（按顺序）
```bash
# 启动API Gateway
java -jar api-gateway/target/api-gateway-1.0-SNAPSHOT.jar

# 启动User Service
java -jar user-service/target/user-service-1.0-SNAPSHOT.jar

# 启动QA Service
java -jar qa-service/target/qa-service-1.0-SNAPSHOT.jar
```

#### 前端服务

```bash
cd frontend
npm install
npm run dev
```

## API文档

### 用户服务 (User Service)

#### 用户注册
```
POST /api/users/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123",
  "nickname": "测试用户"
}
```

#### 用户登录
```
POST /api/users/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

### 问答服务 (QA Service)

#### 提问
```
POST /api/qa/question
Authorization: Bearer <token>
Content-Type: application/json

{
  "question": "什么是人工智能？",
  "userId": 1,
  "sessionId": "session_123",
  "questionType": "general"
}
```

#### 获取用户历史
```
GET /api/qa/history/user/{userId}?page=0&size=10
Authorization: Bearer <token>
```

## 功能特性

### 用户管理
- [x] 用户注册和登录
- [x] JWT身份验证
- [x] 用户信息管理
- [x] 密码加密存储

### 问答功能
- [x] 智能问答处理
- [x] AI模型集成（OpenAI GPT）
- [x] 会话管理
- [x] 问答历史记录
- [x] 响应时间统计

### 系统特性
- [x] 微服务架构
- [x] 服务注册与发现
- [x] API网关路由
- [x] 统一异常处理
- [x] 容器化部署

## 配置说明

### AI模型配置

在 `qa-service/src/main/resources/application.yml` 中配置：

```yaml
ai:
  model:
    api:
      url: https://api.openai.com/v1/chat/completions
      key: ${AI_API_KEY:your-api-key-here}
    timeout: 30000
    max-tokens: 1000
    temperature: 0.7
```

### 数据库配置

系统使用MySQL数据库，表结构会在服务启动时自动创建。

主要数据表：
- `users`: 用户信息
- `qa_records`: 问答记录

### Nacos配置

默认配置：
- 地址: localhost:8848
- 命名空间: ai-qa-system
- 组: DEFAULT_GROUP

## 开发指南

### 添加新的API接口

1. 在相应的Service中添加业务逻辑
2. 在Controller中添加REST端点
3. 更新前端API客户端
4. 添加必要的DTO和验证

### 扩展AI模型支持

1. 在 `AIModelClient` 中添加新的模型调用方法
2. 更新配置文件支持新模型参数
3. 在Service层调用新的AI接口

## 故障排除

### 常见问题

1. **服务无法注册到Nacos**
   - 检查Nacos服务是否正常运行
   - 确认网络连接和端口配置

2. **数据库连接失败**
   - 检查MySQL服务状态
   - 验证数据库连接字符串和凭据

3. **AI接口调用失败**
   - 检查API密钥是否正确设置
   - 确认网络可访问OpenAI服务

4. **前端无法访问后端API**
   - 检查API Gateway是否正常运行
   - 确认跨域配置是否正确

### 查看日志

```bash
# Docker方式查看日志
docker-compose logs -f [service-name]

# 本地运行查看日志
tail -f logs/application.log
```

## 贡献指南

1. Fork项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

MIT License