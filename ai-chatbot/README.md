# AI QA System - Frontend

基于 Next.js 15 的 AI 问答系统前端界面。

## 技术栈

- **框架**: Next.js 15 (App Router)
- **UI**: React 19 + TypeScript + Tailwind CSS
- **认证**: NextAuth.js v5
- **API**: Axios 调用后端微服务

## 快速开始

### 1. 安装依赖

```bash
pnpm install
```

### 2. 配置环境变量

创建 `.env.local` 文件：

```bash
# 认证密钥（必需）
AUTH_SECRET=your-secret-key-here

# API Gateway 地址（必需）
NEXT_PUBLIC_GATEWAY_URL=http://localhost:8083
```

生成密钥：
```bash
openssl rand -base64 32
```

### 3. 启动开发服务器

```bash
pnpm dev
```

访问 http://localhost:3000

## 环境变量

| 变量 | 必需 | 说明 |
|------|------|------|
| `AUTH_SECRET` | ✅ | JWT 签名密钥 |
| `NEXT_PUBLIC_GATEWAY_URL` | ✅ | 后端 API Gateway 地址 |

## 项目结构

```
ai-chatbot/
├── app/                 # Next.js App Router
│   ├── (auth)/         # 认证相关页面
│   ├── (chat)/         # 聊天相关页面
│   ├── layout.tsx      # 根布局
│   └── middleware.ts   # 认证中间件
├── components/         # React 组件
├── lib/               # 工具库和 API 客户端
└── public/            # 静态资源
```

## API 调用流程

1. 浏览器 → Next.js API Routes (`/api/chat/*`)
2. Next.js API Routes → 后端 Gateway (`http://localhost:8083`)
3. Gateway → 微服务 (user-service/qa-service)

## 部署

### 开发环境

```bash
pnpm build
pnpm start
```

### 生产环境

使用 Docker Compose：

```bash
docker compose -f docker-compose.prod.yml up -d
```

## 注意事项

- 确保 API Gateway 运行在 8083 端口
- 不要提交 `.env.local` 文件到版本控制
- 生产环境使用 HTTPS 和强密码

## 相关链接

- [后端 API 文档](http://localhost:8083/swagger-ui.html)
- [项目架构文档](../PROJECT_ARCHITECTURE.md)