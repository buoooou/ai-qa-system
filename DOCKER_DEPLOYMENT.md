# AI QA系统 Docker部署指南

本文档介绍如何使用Docker部署AI QA系统的完整流程。

## 📋 目录结构

```
ai-qa-system/
├── .dockerignore                    # Docker构建忽略文件
├── docker-compose.yml              # Docker Compose编排文件
├── ci.yml                          # CI/CD流水线配置
├── backend-services/               # 后端服务
│   ├── api-gateway/
│   │   └── Dockerfile              # API网关Docker文件
│   ├── user-service/
│   │   └── Dockerfile              # 用户服务Docker文件
│   └── qa-service/
│       └── Dockerfile              # QA服务Docker文件
├── frontend/
│   └── Dockerfile                  # 前端应用Docker文件
└── database/
    ├── Dockerfile                  # MySQL数据库Docker文件
    ├── my.cnf                      # MySQL配置文件
    ├── init-scripts/               # 数据库初始化脚本
    └── README.md                   # 数据库说明文档
```

## 🚀 快速开始

### 1. 本地开发环境部署

```bash
# 克隆项目
git clone <repository-url>
cd ai-qa-system

# 使用Docker Compose启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 2. 单独构建服务

```bash
# 构建MySQL数据库镜像
docker build -t ai-qa-mysql:latest ./database

# 构建API网关镜像
docker build -t ai-qa-gateway:latest ./backend-services/api-gateway

# 构建用户服务镜像
docker build -t ai-qa-user-service:latest ./backend-services/user-service

# 构建QA服务镜像
docker build -t ai-qa-service:latest ./backend-services/qa-service

# 构建前端镜像
docker build -t ai-qa-frontend:latest ./frontend
```

## 🔧 服务配置

### 端口映射

| 服务 | 容器端口 | 主机端口 | 描述 |
|------|----------|----------|------|
| MySQL | 3306 | 3306 | 数据库服务 |
| API Gateway | 8080 | 8080 | API网关 |
| User Service | 8081 | 8081 | 用户服务 |
| QA Service | 8082 | 8082 | QA服务 |
| Frontend | 3000 | 3000 | 前端应用 |

### 环境变量

#### MySQL数据库
- `MYSQL_ROOT_PASSWORD`: root用户密码
- `MYSQL_DATABASE`: 数据库名称
- `MYSQL_USER`: 应用用户名
- `MYSQL_PASSWORD`: 应用用户密码

#### 后端服务
- `SPRING_PROFILES_ACTIVE`: Spring配置文件
- `MYSQL_HOST`: MySQL主机地址
- `MYSQL_PORT`: MySQL端口
- `MYSQL_DATABASE`: 数据库名称
- `MYSQL_USERNAME`: 数据库用户名
- `MYSQL_PASSWORD`: 数据库密码

#### 前端服务
- `NODE_ENV`: 运行环境
- `NEXT_PUBLIC_API_URL`: API服务地址

## 🏥 健康检查

所有服务都配置了健康检查：

- **MySQL**: `mysqladmin ping`
- **后端服务**: Spring Boot Actuator `/actuator/health`
- **前端服务**: 自定义健康检查端点 `/api/health`

## 📊 监控和日志

### 查看服务状态
```bash
# 查看所有服务状态
docker-compose ps

# 查看特定服务日志
docker-compose logs -f [service-name]

# 查看服务健康状态
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8081/actuator/health  # User Service
curl http://localhost:8082/actuator/health  # QA Service
curl http://localhost:3000/api/health       # Frontend
```

### 访问应用
- 前端应用: http://localhost:3000
- API文档: http://localhost:8080/swagger-ui.html
- 用户服务API: http://localhost:8081/swagger-ui.html
- QA服务API: http://localhost:8082/swagger-ui.html

## 🔄 CI/CD集成

项目包含完整的CI/CD配置文件 `ci.yml`，支持：

1. **自动构建**: 代码提交后自动构建所有Docker镜像
2. **自动测试**: 运行单元测试和集成测试
3. **自动部署**: 构建成功后自动部署到目标环境

### CI/CD流程
1. 代码提交触发流水线
2. 构建后端服务JAR包
3. 运行测试套件
4. 构建Docker镜像
5. 推送镜像到仓库
6. 部署到目标环境

## 🛠️ 故障排除

### 常见问题

1. **服务启动失败**
   ```bash
   # 检查服务日志
   docker-compose logs [service-name]
   
   # 重启服务
   docker-compose restart [service-name]
   ```

2. **数据库连接失败**
   ```bash
   # 检查MySQL服务状态
   docker-compose exec mysql mysqladmin ping
   
   # 检查网络连接
   docker network ls
   docker network inspect ai-qa-network
   ```

3. **端口冲突**
   ```bash
   # 检查端口占用
   netstat -tulpn | grep :3306
   
   # 修改docker-compose.yml中的端口映射
   ```

### 清理和重置

```bash
# 停止所有服务
docker-compose down

# 删除所有容器和网络
docker-compose down --volumes --remove-orphans

# 清理未使用的镜像
docker image prune -f

# 完全重置（谨慎使用）
docker system prune -a --volumes
```

## 📝 开发建议

1. **本地开发**: 使用 `docker-compose up -d mysql` 只启动数据库，其他服务在IDE中运行
2. **调试模式**: 在docker-compose.yml中添加调试端口映射
3. **热重载**: 前端服务支持代码热重载，后端服务需要重新构建镜像
4. **数据持久化**: MySQL数据通过Docker卷持久化存储

## 🔒 安全注意事项

1. **生产环境**: 修改默认密码和密钥
2. **网络安全**: 配置防火墙规则，限制端口访问
3. **镜像安全**: 定期更新基础镜像，扫描安全漏洞
4. **敏感信息**: 使用Docker Secrets或环境变量管理敏感配置

## 📞 支持

如有问题，请查看：
1. 项目文档
2. Docker官方文档
3. Spring Boot文档
4. Next.js文档