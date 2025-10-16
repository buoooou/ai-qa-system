# AI QA System - 部署指南

## 概述

本文档详细说明了 AI QA 系统的部署流程，包括开发环境搭建、生产环境部署和运维管理。

## 系统要求

### 最低配置
- **CPU**: 4 核心
- **内存**: 8GB RAM
- **存储**: 50GB 可用空间
- **网络**: 稳定的互联网连接

### 推荐配置
- **CPU**: 8 核心
- **内存**: 16GB RAM
- **存储**: 100GB SSD
- **网络**: 带宽 ≥ 100Mbps

### 软件依赖
- Docker 20.10+
- Docker Compose 2.0+
- Git 2.30+
- Java 17+ (本地开发)
- Node.js 18+ (本地开发)

## 一、快速开始

### 1. 克隆项目

```bash
git clone https://github.com/<your-org>/ai-qa-system.git
cd ai-qa-system
```

### 2. 配置环境变量

创建环境变量文件：

```bash
# 复制模板
cp .env.example .env

# 编辑配置
vim .env
```

**必需的环境变量**：
```bash
# 数据库配置
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_DATABASE=ai_qa_system
MYSQL_USER=ai_user
MYSQL_PASSWORD=your_user_password

# JWT 密钥（生成命令：openssl rand -base64 32）
JWT_SECRET=your_jwt_secret_here

# AI 服务配置
GEMINI_API_KEY=your_gemini_api_key

# 服务发现
NACOS_SERVER_ADDR=localhost:8848

# 前端配置
NEXT_PUBLIC_GATEWAY_URL=http://localhost:8083
AUTH_SECRET=your_auth_secret_here

# Docker Hub（部署需要）
DOCKERHUB_USERNAME=your_dockerhub_username
DOCKERHUB_TOKEN=your_dockerhub_token
```

### 3. 启动服务

```bash
# 启动所有服务
docker compose up -d

# 查看服务状态
docker compose ps

# 查看日志
docker compose logs -f
```

### 4. 验证部署

访问以下地址验证服务：

- **前端应用**: http://localhost:3000
- **API Gateway**: http://localhost:8083/swagger-ui.html
- **用户服务**: http://localhost:8081/actuator/health
- **QA 服务**: http://localhost:8082/actuator/health
- **Nacos 控制台**: http://localhost:8848/nacos (nacos/nacos)

## 二、本地开发环境

### 2.1 前端开发

```bash
cd ai-chatbot

# 安装依赖
pnpm install

# 配置环境变量
cat > .env.local << EOF
AUTH_SECRET=your-local-secret
NEXT_PUBLIC_GATEWAY_URL=http://localhost:8083
EOF

# 启动开发服务器
pnpm dev
```

### 2.2 后端开发

```bash
cd backend-services

# 编译项目
mvn clean compile

# 运行测试
mvn test

# 启动单个服务（需要先启动 MySQL 和 Nacos）
mvn spring-boot:run -pl user-service
mvn spring-boot:run -pl qa-service
mvn spring-boot:run -pl api-gateway
```

### 2.3 调试配置

**IDEA IntelliJ 配置**：
1. 导入项目为 Maven 项目
2. 设置 JDK 17
3. 配置运行/调试配置
4. 添加 VM 参数：`-Dspring.profiles.active=dev`

**VS Code 配置**：
```json
// .vscode/settings.json
{
  "java.home": "/path/to/jdk-17",
  "java.configuration.updateBuildConfiguration": "automatic",
  "spring-boot.ls.checkJVM": false
}
```

## 三、生产环境部署

### 3.1 服务器准备

#### EC2 实例初始化

```bash
# 更新系统
sudo yum update -y

# 安装 Docker
sudo yum install -y docker
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -a -G docker ec2-user

# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 验证安装
docker --version
docker-compose --version
```

#### 防火墙配置

```bash
# 开放必要端口
sudo ufw allow 22    # SSH
sudo ufw allow 80    # HTTP
sudo ufw allow 443   # HTTPS
sudo ufw allow 3000  # 前端（可选）
sudo ufw allow 8083  # API Gateway（可选）
sudo ufw enable
```

### 3.2 手动部署

#### 1. 克隆代码

```bash
# SSH 到服务器
ssh -i /path/to/key.pem ec2-user@your-server-ip

# 克隆项目
git clone https://github.com/<your-org>/ai-qa-system.git
cd ai-qa-system
```

#### 2. 配置生产环境变量

```bash
# 创建生产环境配置
cp deploy/env.example .env

# 编辑配置（使用强密码）
vim .env
```

**生产环境特别注意**：
```bash
# 使用强密码
MYSQL_ROOT_PASSWORD=$(openssl rand -base64 32)
MYSQL_PASSWORD=$(openssl rand -base64 32)
JWT_SECRET=$(openssl rand -base64 32)

# 生产环境服务地址
NACOS_SERVER_ADDR=nacos:8848
```

#### 3. 创建 Docker 网络

```bash
# 创建外部网络
docker network create ai-qa-net
```

#### 4. 启动 Nacos（独立）

```bash
# 启动 Nacos 服务
docker run -d \
  --name nacos \
  --network ai-qa-net \
  -p 8848:8848 \
  -e MODE=standalone \
  nacos/nacos-server:v2.2.3

# 等待 Nacos 启动
sleep 30
```

#### 5. 部署应用服务

```bash
# 使用生产配置启动
docker compose -f docker-compose.prod.yml up -d

# 查看服务状态
docker compose -f docker-compose.prod.yml ps

# 查看日志
docker compose -f docker-compose.prod.yml logs -f
```

#### 6. 配置 Nginx（可选）

创建 Nginx 配置文件 `/etc/nginx/conf.d/ai-qa.conf`：

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端
    location / {
        proxy_pass http://localhost:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # API
    location /api/ {
        proxy_pass http://localhost:8083;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # WebSocket 支持
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

启动 Nginx：
```bash
sudo systemctl start nginx
sudo systemctl enable nginx
```

### 3.3 CI/CD 自动部署

项目配置了 GitHub Actions 自动部署流程。

#### 触发条件
- Push 到 `main` 分支
- Pull Request 到 `main` 分支

#### 部署流程

1. **构建阶段**
   ```yaml
   - Maven 构建后端服务
   - Docker 构建镜像
   - 推送到 Docker Hub
   ```

2. **部署阶段**
   ```yaml
   - SSH 连接到 EC2
   - 拉取最新代码
   - 更新环境变量
   - Docker Compose 部署
   - 健康检查
   ```

#### 配置 GitHub Secrets

在 GitHub 仓库设置中配置以下 Secrets：

| Secret 名称 | 说明 | 示例 |
|------------|------|------|
| `DOCKERHUB_USERNAME` | Docker Hub 用户名 | `yourusername` |
| `DOCKERHUB_TOKEN` | Docker Hub 访问令牌 | `dckr_pat_...` |
| `EC2_HOST` | EC2 服务器地址 | `13.230.43.180` |
| `EC2_USERNAME` | SSH 用户名 | `ec2-user` |
| `SSH_PRIVATE_KEY` | SSH 私钥 | `-----BEGIN...` |
| `EC2_PORT` | SSH 端口 | `22` |
| `JWT_SECRET` | JWT 密钥 | `your-secret` |
| `GEMINI_API_KEY` | Gemini API Key | `AIzaSy...` |
| `NACOS_SERVER_ADDR` | Nacos 地址 | `nacos:8848` |

## 四、运维管理

### 4.1 服务监控

#### 健康检查脚本

创建 `scripts/health-check.sh`：

```bash
#!/bin/bash

# 服务列表
services=("frontend:3000" "api-gateway:8083" "user-service:8081" "qa-service:8082")

for service in "${services[@]}"; do
    name=$(echo $service | cut -d: -f1)
    port=$(echo $service | cut -d: -f2)

    if curl -f -s http://localhost:$port/actuator/health > /dev/null; then
        echo "✅ $name is healthy"
    else
        echo "❌ $name is unhealthy"
        # 发送告警
        # curl -X POST "https://api.telegram.org/bot<TOKEN>/sendMessage" -d "chat_id=<CHAT_ID>&text=$name is down"
    fi
done
```

设置定时任务：
```bash
# 每5分钟检查一次
*/5 * * * * /path/to/health-check.sh >> /var/log/health-check.log 2>&1
```

#### 日志管理

```bash
# 查看实时日志
docker compose logs -f

# 查看特定服务日志
docker compose logs -f api-gateway

# 日志轮转配置
sudo vim /etc/logrotate.d/docker-compose
```

### 4.2 备份策略

#### 数据库备份

创建备份脚本 `scripts/backup-mysql.sh`：

```bash
#!/bin/bash

BACKUP_DIR="/backup/mysql"
DATE=$(date +%Y%m%d_%H%M%S)
CONTAINER_NAME="ai-qa-mysql"

# 创建备份目录
mkdir -p $BACKUP_DIR

# 执行备份
docker exec $CONTAINER_NAME mysqldump -u root -p$MYSQL_ROOT_PASSWORD ai_qa_system > $BACKUP_DIR/backup_$DATE.sql

# 压缩备份
gzip $BACKUP_DIR/backup_$DATE.sql

# 删除7天前的备份
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +7 -delete

echo "Backup completed: backup_$DATE.sql.gz"
```

设置定时备份：
```bash
# 每天凌晨2点备份
0 2 * * * /path/to/backup-mysql.sh
```

#### 恢复数据库

```bash
# 停止相关服务
docker compose stop user-service qa-service

# 恢复备份
gunzip < backup_20250116_020000.sql.gz | docker exec -i ai-qa-mysql mysql -u root -p ai_qa_system

# 重启服务
docker compose start user-service qa-service
```

### 4.3 性能优化

#### JVM 调优

在 `docker-compose.prod.yml` 中添加 JVM 参数：

```yaml
environment:
  - JAVA_OPTS=-Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

#### 数据库优化

MySQL 配置优化 `my.cnf`：

```ini
[mysqld]
# 内存配置
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M

# 连接数
max_connections = 200
max_connect_errors = 1000

# 查询缓存
query_cache_type = 1
query_cache_size = 64M

# 慢查询日志
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2
```

### 4.4 故障处理

#### 常见问题及解决方案

1. **服务启动失败**
   ```bash
   # 检查端口占用
   netstat -tulpn | grep :8083

   # 查看详细日志
   docker compose logs api-gateway --tail=100

   # 重启服务
   docker compose restart api-gateway
   ```

2. **数据库连接失败**
   ```bash
   # 检查 MySQL 状态
   docker compose ps mysql

   # 测试连接
   docker exec -it ai-qa-mysql mysql -u root -p

   # 检查网络连通性
   docker network ls
   docker network inspect ai-qa-net
   ```

3. **内存不足**
   ```bash
   # 查看资源使用
   docker stats

   # 清理未使用的镜像
   docker system prune -a

   # 增加 swap 空间
   sudo fallocate -l 2G /swapfile
   sudo chmod 600 /swapfile
   sudo mkswap /swapfile
   sudo swapon /swapfile
   ```

#### 紧急恢复流程

1. **服务全部宕机**
   ```bash
   # 检查 Docker 状态
   sudo systemctl status docker

   # 重启 Docker
   sudo systemctl restart docker

   # 重新启动所有服务
   docker compose -f docker-compose.prod.yml up -d
   ```

2. **数据丢失**
   ```bash
   # 从最新备份恢复
   ./scripts/restore-from-backup.sh latest
   ```

## 五、安全加固

### 5.1 网络安全

```bash
# 配置 iptables 规则
sudo iptables -A INPUT -p tcp --dport 22 -j ACCEPT
sudo iptables -A INPUT -p tcp --dport 80 -j ACCEPT
sudo iptables -A INPUT -p tcp --dport 443 -j ACCEPT
sudo iptables -A INPUT -j DROP
```

### 5.2 SSL/TLS 配置

使用 Let's Encrypt 配置 HTTPS：

```bash
# 安装 Certbot
sudo yum install -y certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d your-domain.com

# 自动续期
echo "0 12 * * * /usr/bin/certbot renew --quiet" | sudo crontab -
```

### 5.3 访问控制

```bash
# 限制 SSH 访问
echo "AllowUsers ec2-user" >> /etc/ssh/sshd_config
echo "PasswordAuthentication no" >> /etc/ssh/sshd_config
sudo systemctl restart sshd

# 配置 fail2ban
sudo yum install -y fail2ban
sudo systemctl enable fail2ban
sudo systemctl start fail2ban
```

## 六、更新与维护

### 6.1 应用更新

```bash
# 拉取最新代码
git pull origin main

# 重新构建并部署
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d --force-recreate

# 清理旧镜像
docker image prune -f
```

### 6.2 系统更新

```bash
# 更新系统包
sudo yum update -y

# 更新 Docker
sudo yum update -y docker

# 重启服务（如果需要）
sudo systemctl restart docker
```

## 七、监控告警

### 7.1 Prometheus + Grafana（可选）

```yaml
# docker-compose.monitoring.yml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3001:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
```

### 7.2 告警配置

创建 Prometheus 告警规则：

```yaml
# alerts.yml
groups:
  - name: ai-qa-system
    rules:
      - alert: ServiceDown
        expr: up == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Service {{ $labels.instance }} is down"

      - alert: HighCPUUsage
        expr: cpu_usage > 80
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High CPU usage on {{ $labels.instance }}"
```

## 八、附录

### 8.1 端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| Frontend | 3000 | Next.js 应用 |
| API Gateway | 8083 | Spring Cloud Gateway |
| User Service | 8081 | 用户管理服务 |
| QA Service | 8082 | AI 问答服务 |
| MySQL | 3306 | 数据库 |
| Nacos | 8848 | 服务注册中心 |

### 8.2 目录结构

```
ai-qa-system/
├── ai-chatbot/              # 前端应用
├── backend-services/        # 后端服务
│   ├── api-gateway/        # API 网关
│   ├── user-service/       # 用户服务
│   ├── qa-service/         # QA 服务
│   └── pom.xml             # Maven 配置
├── deploy/                  # 部署脚本
│   ├── env.example         # 环境变量模板
│   └── deploy.sh           # 部署脚本
├── scripts/                 # 运维脚本
│   ├── backup-mysql.sh     # 数据库备份
│   └── health-check.sh     # 健康检查
├── docker-compose.yml      # 开发环境
├── docker-compose.prod.yml # 生产环境
├── .github/workflows/      # CI/CD 配置
├── PROJECT_ARCHITECTURE.md # 架构文档
└── DEPLOYMENT.md           # 部署文档
```

### 8.3 有用的命令

```bash
# Docker 命令
docker ps -a                    # 查看所有容器
docker logs <container>         # 查看日志
docker exec -it <container> bash # 进入容器
docker stats                    # 资源使用情况

# Docker Compose 命令
docker compose up -d            # 后台启动
docker compose down             # 停止并删除
docker compose restart <service> # 重启服务
docker compose logs -f <service> # 查看日志

# 系统监控
top                             # CPU/内存使用
df -h                           # 磁盘使用
free -h                         # 内存使用
netstat -tulpn                  # 网络连接
```

## 联系支持

- **技术支持**: support@your-domain.com
- **GitHub Issues**: https://github.com/<your-org>/ai-qa-system/issues
- **文档**: https://docs.your-domain.com

---

## 更新日志

- **2025-01-15**: 初始部署文档
- **2025-01-16**: 更新环境变量配置，统一使用 NEXT_PUBLIC_GATEWAY_URL

最后更新：2025-01-16