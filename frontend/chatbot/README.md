This is a [Next.js](https://nextjs.org) project bootstrapped with [`create-next-app`](https://nextjs.org/docs/app/api-reference/cli/create-next-app).

## 环境变量

在根目录创建 `.env.local`：

```
BACKEND_BASE_URL=http://localhost:8080
NEXT_PUBLIC_DEFAULT_USER_ID=1
```

## 开发

```bash
npm run dev
```

打开浏览器访问 `http://localhost:3000/chat`。

## 功能特性

- **用户认证**：登录/注册功能，支持用户身份管理
- **聊天界面**：与 AI 进行对话，支持消息历史
- **响应式设计**：适配桌面和移动设备
- **深色模式**：自动适配系统主题

## API 路由

### 认证相关
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

### 聊天相关  
- `POST /api/chat` - 发送消息并获取 AI 回答

## 后端接口

前端通过网关与后端交互：

- `POST ${BACKEND_BASE_URL}/api/user/login` - 用户登录
- `POST ${BACKEND_BASE_URL}/api/user/register` - 用户注册  
- `GET ${BACKEND_BASE_URL}/api/qa/test` - 获取回答（示例）
- `POST ${BACKEND_BASE_URL}/api/qa/save` - 保存问答历史（忽略失败）

## Getting Started

First, run the development server:

```bash
npm run dev
# or
yarn dev
# or
pnpm dev
# or
bun dev
```

Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

You can start editing the page by modifying `app/page.tsx`. The page auto-updates as you edit the file.

This project uses [`next/font`](https://nextjs.org/docs/app/building-your-application/optimizing/fonts) to automatically optimize and load [Geist](https://vercel.com/font), a new font family for Vercel.

## Learn More

To learn more about Next.js, take a look at the following resources:

- [Next.js Documentation](https://nextjs.org/docs) - learn about Next.js features and API.
- [Learn Next.js](https://nextjs.org/learn) - an interactive Next.js tutorial.

You can check out [the Next.js GitHub repository](https://github.com/vercel/next.js) - your feedback and contributions are welcome!

## Deploy on Vercel

The easiest way to deploy your Next.js app is to use the [Vercel Platform](https://vercel.com/new?utm_medium=default-template&filter=next.js&utm_source=create-next-app&utm_campaign=create-next-app-readme) from the creators of Next.js.

Check out our [Next.js deployment documentation](https://nextjs.org/docs/app/building-your-application/deploying) for more details.
