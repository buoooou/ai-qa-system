# 环境变量配置指南

## 📋 概述

本项目使用环境变量来管理敏感配置信息，如API密钥、数据库密码等，确保安全性和灵活性。

## 🔧 快速设置

### 1. 自动设置（推荐）
```bash
cd ai-qa-system
./setup-env.sh
```

### 2. 手动设置
```bash
# 复制环境变量模板
cp .env.example .env

# 编辑.env文件，设置您的配置
nano .env  # 或使用其他编辑器
```

## 🔑 必需的环境变量

### Gemini API Key
```bash
GEMINI_API_KEY=your_actual_api_key_here
```

**获取方式：**
1. 访问 [Google AI Studio](https://makersuite.google.com/app/apikey)
2. 登录您的Google账号
3. 点击"Create API Key"
4. 复制生成的API Key

### 数据库配置
```bash
MYSQL_ROOT_PASSWORD=root123
MYSQL_DATABASE=ai_qa_system
MYSQL_USER=aiqa
MYSQL_PASSWORD=aiqa123
```

## 🚀 使用方法

### Docker Compose（推荐）
```bash
# 确保.env文件已配置
docker-compose up -d
```

### 本地开发
```bash
# 加载环境变量
source .env

# 启动服务
cd backend-services/qa-service
mvn spring-boot:run
```

### IDE开发
在您的IDE中设置环境变量：
- **IntelliJ IDEA**: Run Configuration → Environment Variables
- **VS Code**: launch.json中的env配置
- **Eclipse**: Run Configuration → Environment

## 🔍 验证配置

### 检查Docker Compose配置
```bash
docker-compose config
```

### 检查环境变量是否生效
```bash
# 查看QA Service的环境变量
docker exec ai-qa-qa-service env | grep GEMINI
```

### 测试API连接
```bash
# 通过Swagger测试
curl -X POST "http://localhost:8080/api/qa/ask" \
  -H "Content-Type: application/json" \
  -d '{"question": "Hello, how are you?"}'
```

## 🛡️ 安全注意事项

### ✅ 已实施的安全措施
- `.env` 文件已添加到 `.gitignore`
- 提供 `.env.example` 作为模板
- 敏感信息不会提交到Git仓库

### ⚠️ 安全建议
1. **不要**将真实的API Key提交到Git
2. **定期轮换**API Key
3. **限制**API Key的使用权限
4. **监控**API使用情况

## 🔄 环境变量更新

### 更新API Key
```bash
# 编辑.env文件
nano .env

# 重启相关服务
docker-compose restart qa-service
```

### 添加新的环境变量
1. 在 `.env.example` 中添加新变量
2. 在 `docker-compose.yml` 中配置
3. 在应用配置中使用 `${VARIABLE_NAME}`

## 🐛 故障排除

### 问题：API调用失败
```bash
# 检查API Key是否正确设置
docker logs ai-qa-qa-service | grep -i gemini
```

### 问题：环境变量未生效
```bash
# 检查.env文件格式
cat .env

# 确保没有多余的空格或引号
# 正确格式：GEMINI_API_KEY=your_key_here
# 错误格式：GEMINI_API_KEY = "your_key_here"
```

### 问题：Docker Compose读取不到环境变量
```bash
# 确保.env文件在docker-compose.yml同级目录
ls -la | grep env

# 重新构建并启动
docker-compose down
docker-compose up --build -d
```

## 📚 相关文件

- `.env` - 实际环境变量配置（不提交到Git）
- `.env.example` - 环境变量模板
- `setup-env.sh` - 自动设置脚本
- `docker-compose.yml` - Docker服务配置
- `.gitignore` - Git忽略文件配置

## 🆘 获取帮助

如果遇到问题，请检查：
1. `.env` 文件是否存在且格式正确
2. API Key是否有效
3. Docker服务是否正常运行
4. 网络连接是否正常

---

**注意：** 请妥善保管您的API Key，不要在公共场所或聊天记录中分享。
