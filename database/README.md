# AI问答系统 - MySQL数据库镜像

这个目录包含了为AI问答系统构建自定义MySQL Docker镜像所需的所有文件。

## 目录结构

```
database/
├── Dockerfile              # Docker镜像构建文件
├── my.cnf                  # MySQL配置文件
├── init-scripts/           # 数据库初始化脚本目录
│   └── 01-init-database.sql # 数据库和表结构初始化脚本
└── README.md              # 说明文档
```

## 功能特性

### 1. 自动数据库初始化
- 自动创建 `ai_qa_system` 数据库
- 创建用户表 (`user`) 和问答历史表 (`qa_history`)
- 插入测试数据，包括管理员和测试用户
- 创建必要的索引以优化查询性能

### 2. 优化配置
- 字符集设置为 `utf8mb4`，支持完整的Unicode字符
- 时区设置为东八区 (`+08:00`)
- 性能优化配置，包括连接池、缓冲区等
- 启用慢查询日志，便于性能调优

### 3. 安全配置
- 设置严格的SQL模式
- 配置二进制日志用于数据备份和恢复
- 合理的权限设置

## 环境变量

镜像使用以下环境变量：

- `MYSQL_ROOT_PASSWORD=ai_qa_system` - root用户密码
- `MYSQL_DATABASE=ai_qa_system` - 默认数据库名
- `MYSQL_USER=ai_qa_user` - 普通用户名
- `MYSQL_PASSWORD=ai_qa_system` - 普通用户密码

## 使用方法

### 1. 构建镜像

```bash
# 在项目根目录执行
docker build -t ai-qa-mysql:latest ./database
```

### 2. 运行容器

```bash
# 运行MySQL容器
docker run -d \
  --name ai-qa-mysql \
  -p 3306:3306 \
  -v mysql_data:/var/lib/mysql \
  ai-qa-mysql:latest
```

### 3. 连接数据库

```bash
# 使用MySQL客户端连接
mysql -h localhost -P 3306 -u root -pai_qa_system ai_qa_system
```

## 测试数据

初始化脚本会创建以下测试数据：

### 用户数据
- **管理员**: username=`admin`, password=`123456`, nick=`管理员`
- **测试用户**: username=`testuser`, password=`123456`, nick=`测试用户`

### 问答数据
- 包含3条测试问答记录，涵盖AI、Spring Boot和编程学习等话题

## 数据库表结构

### user表（用户表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID，自增 |
| username | VARCHAR(255) | 用户名，唯一 |
| password | VARCHAR(255) | 加密后的密码 |
| nick | VARCHAR(255) | 用户昵称 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### qa_history表（问答历史表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID，自增 |
| user_id | BIGINT | 用户ID，外键 |
| question | TEXT | 用户提出的问题 |
| answer | LONGTEXT | AI返回的回答 |
| create_time | DATETIME | 创建时间 |

## 注意事项

1. **密码安全**: 生产环境中请修改默认密码
2. **数据持久化**: 建议使用Docker卷来持久化数据
3. **性能调优**: 根据实际负载调整my.cnf中的配置参数
4. **备份策略**: 定期备份数据库，二进制日志已启用
5. **监控**: 建议配置数据库监控和告警

## CI/CD集成

该MySQL镜像已集成到GitHub Actions CI/CD流水线中：
- 自动构建镜像
- 推送到Docker Hub
- 支持多环境部署

镜像标签格式：`{DOCKERHUB_USERNAME}/ai-qa-mysql:latest`