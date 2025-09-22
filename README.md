# AI智能问答客服系统

基于微服务架构的AI智能问答系统，集成Google Gemini API，提供智能对话服务。

## 🏗️ 项目架构

```
ai-qa-system/
├── backend-services/           # 后端微服务
│   ├── api-gateway/           # API网关服务 (端口: 8080)
│   ├── user-service/          # 用户管理服务 (端口: 8081)
│   ├── qa-service/           # 问答服务 (端口: 8082)
│   └── pom.xml               # Maven父项目配置
├── frontend/                 # Next.js前端应用 (端口: 3000)
├── docker-compose.yml        # Docker容器编排
└── README.md                # 项目说明文档
```

## 🚀 技术栈

### 后端技术
- **Spring Boot 2.7.17** - 微服务框架
- **Spring Cloud Gateway** - API网关
- **Spring Cloud OpenFeign** - 服务间通信
- **MyBatis** - 数据持久化
- **MySQL** - 关系型数据库
- **Google Gemini API** - AI智能问答

### 前端技术
- **Next.js 15.5.3** - React全栈框架
- **TypeScript** - 类型安全
- **Tailwind CSS** - 样式框架
- **Axios** - HTTP客户端

### 部署技术
- **Docker** - 容器化
- **Docker Compose** - 容器编排
- **GitHub Actions** - CI/CD流水线

## 📋 功能特性

### 🔐 用户管理
- ✅ 用户注册/登录
- ✅ JWT Token认证
- ✅ 用户信息管理
- ✅ 密码加密存储

### 🤖 AI问答功能
- ✅ 集成Google Gemini API
- ✅ 智能对话问答
- ✅ 问答历史记录
- ✅ 个性化回答（基于用户信息）
- ✅ 对话上下文管理

### 🌐 前端界面
- ✅ 现代化响应式设计
- ✅ 实时聊天界面
- ✅ 用户友好的登录/注册页面
- ✅ 历史记录管理
- ✅ 错误处理和加载状态

### 🏗️ 微服务架构
- ✅ API Gateway统一入口
- ✅ 服务间通信（Feign Client）
- ✅ 负载均衡和容错
- ✅ 统一配置管理

## 🛠️ 快速开始

### 环境要求
- Java 17+
- Node.js 18+
- MySQL 8.0+
- Docker & Docker Compose (可选)

### 1. 克隆项目
```bash
git clone <repository-url>
cd ai-qa-system
```

### 2. 配置数据库
```sql
-- 创建数据库
CREATE DATABASE ai_qa_system;

-- 用户服务数据库
CREATE DATABASE user_service;
```

### 3. 配置Gemini API Key
```bash
# 在qa-service的application.yml中配置
gemini:
  api:
    key: YOUR_GEMINI_API_KEY
    url: https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent
```

### 4. 启动后端服务

#### 方式一：使用Maven
```bash
# 启动用户服务
cd backend-services/user-service
mvn spring-boot:run

# 启动问答服务
cd ../qa-service
mvn spring-boot:run

# 启动API网关
cd ../api-gateway
mvn spring-boot:run
```

#### 方式二：使用Docker Compose
```bash
docker-compose up -d
```

### 5. 启动前端应用
```bash
cd frontend
npm install
npm run dev
```

### 6. 访问应用
- 前端应用: http://localhost:3000
- API网关: http://localhost:8080
- 用户服务: http://localhost:8081
- 问答服务: http://localhost:8082

## 🔧 配置说明

### 后端配置
各服务的配置文件位于 `src/main/resources/application.yml`

### 前端配置
环境变量配置文件：`.env.local`
```bash
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

## 📡 API接口文档

### 用户服务 API
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/{id}` - 获取用户信息
- `GET /api/user/health` - 健康检查

### 问答服务 API
- `POST /api/qa/ask` - 提交问题
- `GET /api/qa/history/{userId}` - 获取问答历史
- `GET /api/qa/history/{userId}/paged` - 分页获取历史
- `DELETE /api/qa/{id}` - 删除问答记录
- `GET /api/qa/health` - 健康检查

## 🐳 Docker部署

### 构建镜像
```bash
# 构建所有服务镜像
docker-compose build

# 启动所有服务
docker-compose up -d
```

### 查看服务状态
```bash
docker-compose ps
docker-compose logs -f
```

## 🔍 测试指南

### 后端测试
```bash
# 健康检查
curl http://localhost:8080/api/user/health
curl http://localhost:8080/api/qa/health

# 用户注册
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"userName":"testuser","password":"password123","confirmPassword":"password123","email":"test@example.com"}'
```

### 前端测试
1. 访问 http://localhost:3000
2. 注册新用户账户
3. 登录系统
4. 测试AI问答功能

## 🚀 部署到生产环境

### 1. 环境变量配置
```bash
# 生产环境配置
export SPRING_PROFILES_ACTIVE=prod
export GEMINI_API_KEY=your_production_api_key
export DATABASE_URL=your_production_database_url
```

### 2. 构建生产镜像
```bash
docker-compose -f docker-compose.prod.yml build
docker-compose -f docker-compose.prod.yml up -d
```

## 🔧 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查数据库服务是否启动
   - 验证数据库连接配置

2. **Gemini API调用失败**
   - 检查API Key是否正确
   - 验证网络连接

3. **服务间通信失败**
   - 检查服务发现配置
   - 验证端口是否被占用

4. **前端API调用失败**
   - 检查API Gateway是否启动
   - 验证CORS配置

## 📈 性能优化

### 后端优化
- 数据库连接池配置
- 缓存策略实施
- API响应时间监控

### 前端优化
- 代码分割和懒加载
- 图片优化
- CDN部署

## 🤝 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

如有问题或建议，请通过以下方式联系：
- 项目Issues: [GitHub Issues](https://github.com/your-repo/issues)
- 邮箱: your-email@example.com

---

**🎉 感谢使用AI智能问答客服系统！**
