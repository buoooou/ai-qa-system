# GitHub Actions CI/CD 工作流配置

本项目包含一个综合的 CI/CD 工作流：`ci-cd.yml`

## 工作流功能

**触发条件：**
- 推送到 `main`、`develop` 或 `feature/yulong` 分支
- 创建 Pull Request 到 `main` 分支
- 手动触发（workflow_dispatch）

**包含的步骤：**
1. **代码质量检查** - 构建和测试前端/后端
2. **Docker 镜像构建** - 构建并推送到 GitHub Container Registry
3. **安全扫描** - 使用 Trivy 进行漏洞扫描
4. **自动部署** - 根据分支自动部署到不同环境
5. **健康检查** - 验证部署结果

## 配置要求

### 1. GitHub Secrets 配置

在 GitHub 仓库设置中添加以下 Secrets：

```
EC2_HOST: 3.84.225.222
EC2_USER: ubuntu
EC2_KEY: EC2 实例的 SSH 私钥内容
```

### 2. EC2 实例准备

确保 EC2 实例已安装：
- Docker
- Docker Compose
- Git

### 3. SSH 密钥配置

1. 在本地生成 SSH 密钥对（如果还没有）：
```bash
ssh-keygen -t rsa -b 4096 -C "your-email@example.com"
```

2. 将公钥添加到 EC2 实例的 `~/.ssh/authorized_keys`：
```bash
ssh-copy-id -i ~/.ssh/id_rsa.pub ubuntu@3.84.225.222
```

3. 将私钥内容复制到 GitHub Secrets 中的 `SSH_PRIVATE_KEY`

### 4. 安全组配置

确保 AWS EC2 安全组开放以下端口：
- 80 (HTTP)
- 8080 (API Gateway)
- 8081 (User Service)
- 8082 (QA Service)
- 5432 (PostgreSQL)
- 6379 (Redis)
- 8848 (Nacos)

## 使用方法

### 自动部署
推送到 `main` 分支会自动触发部署。

### 手动部署
1. 进入 GitHub 仓库的 Actions 页面
2. 选择 "Deploy to AWS EC2" 工作流
3. 点击 "Run workflow"
4. 选择环境（prod 或 staging）
5. 点击 "Run workflow"

### 查看部署状态
- 在 Actions 页面查看工作流执行状态
- 查看日志了解部署详情
- 部署成功后访问 `http://3.84.225.222`

## 故障排除

### 常见问题

1. **SSH 连接失败**
   - 检查 SSH_PRIVATE_KEY secret 是否正确
   - 确认 EC2 实例的 SSH 服务正在运行

2. **Docker 构建失败**
   - 检查 Dockerfile 语法
   - 确认所有依赖文件存在

3. **服务启动失败**
   - 查看容器日志：`docker-compose logs`
   - 检查端口占用：`netstat -tlnp`

4. **健康检查失败**
   - 确认所有服务都已启动
   - 检查网络连接和防火墙设置

### 调试命令

在 EC2 实例上执行以下命令进行调试：

```bash
# 查看容器状态
docker-compose ps

# 查看容器日志
docker-compose logs [service-name]

# 检查端口占用
netstat -tlnp | grep -E ':(80|8080|8081|8082)'

# 测试服务连通性
curl http://localhost/register/
curl http://localhost:8080/actuator/health
```
