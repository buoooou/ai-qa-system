# AI问答系统前端项目总结

## 项目概述

这是一个基于 Next.js 15 构建的 AI 问答系统前端项目，使用了 React 19 和 TypeScript。项目采用了现代化的前端技术栈，实现了用户认证、AI聊天对话等核心功能。

## 技术栈

### 核心框架
- **Next.js 15**: React 框架，使用 App Router 架构
- **React 19**: 用于构建用户界面的 JavaScript 库
- **TypeScript**: JavaScript 的超集，提供类型安全

### UI 组件和样式
- **Tailwind CSS**: 实用优先的 CSS 框架
- **Radix UI**: 无样式的可访问 UI 组件
- **Lucide React**: 图标库

### AI 集成
- **@ai-sdk/google**: Google AI 模型集成
- **ai**: Vercel AI SDK，用于 AI 流式处理

### 状态管理和数据获取
- **@ai-sdk/react**: React 钩子用于 AI 集成
- **next-auth**: 认证解决方案

### 开发工具
- **ESLint**: 代码质量检查工具
- **Jest**: JavaScript 测试框架
- **Prettier**: 代码格式化工具

## 业务功能要点

### 1. 用户认证系统
- 用户注册和登录功能
- JWT Token 认证机制
- 路由保护（Protected Routes）
- 本地存储用户凭证

### 2. AI 聊天功能
- 实时与 Google Gemini AI 模型交互
- 流式消息显示（打字机效果）
- 支持 Markdown 格式渲染
- 代码高亮显示

### 3. 对话管理
- 多对话管理（创建、选择、删除、重命名）
- 对话历史记录
- 自动根据第一条消息生成对话标题

### 4. 用户界面
- 响应式设计，适配不同设备
- 侧边栏对话列表
- 聊天窗口主界面
- 加载状态和错误处理

## 项目架构

### 目录结构
```
app/                  # 页面路由
  api/                # API 路由
  auth/               # 认证页面
  hello/              # 示例页面
components/           # 可复用组件
  auth/               # 认证相关组件
  ui/                 # UI 基础组件
contexts/             # React Context
hooks/                # 自定义钩子
lib/                  # 工具库
types/                # TypeScript 类型定义
tests/                # 测试文件
```

### 核心概念

1. **页面和路由**:
   - 使用 Next.js App Router
   - 页面组件位于 `app/*/page.tsx`
   - API 路由位于 `app/api/*/route.ts`

2. **状态管理**:
   - 使用 React Context 进行全局状态管理（如认证状态）
   - 组件内部状态使用 useState 和 useReducer

3. **数据流**:
   - 页面组件管理主要应用状态
   - 通过 API 路由或直接调用后端服务获取数据
   - AI 聊天通过 Vercel AI SDK 实现流式交互

## 部署要求

1. **环境变量**:
   - Google AI API 密钥
   - JWT 密钥
   - 后端服务地址

2. **构建和运行**:
   - Node.js 18+
   - 支持 Next.js 部署的平台（Vercel, Netlify等）

## 开发指南

1. 安装依赖: `npm install`
2. 启动开发服务器: `npm run dev`
3. 构建生产版本: `npm run build`
4. 启动生产服务器: `npm run start`

## 集成后端服务

该项目设计为与微服务架构集成:
- 用户认证服务
- AI 问答服务
- 可通过 API 路由或直接 HTTP 调用与后端交互