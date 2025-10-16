# 前端性能优化指南

## 问题诊断

当前前端应用可能存在以下性能问题：

1. **使用 Next.js 15 Canary 版本** - 不稳定
2. **依赖过多** - 包含大量未使用的包
3. **开发模式配置不当** - Turbo 模式可能影响性能

## 优化方案

### 方案一：使用稳定版本（推荐）

```bash
# 备份当前配置
cp package.json package.json.backup
cp next.config.ts next.config.ts.backup

# 使用优化配置
cp package.optimized.json package.json
cp next.config.optimized.ts next.config.ts

# 重新安装依赖
pnpm install

# 启动开发服务器
pnpm dev
```

### 方案二：保留现有版本但优化

#### 1. 移除不必要的依赖

```bash
# 移除未使用的包
pnpm remove @codemirror/lang-javascript @codemirror/lang-python
pnpm remove @codemirror/state @codemirror/theme-one-dark
pnpm remove @codemirror/view codemirror
pnpm remove @opentelemetry/api @opentelemetry/api-logs @vercel/otel
pnpm remove @vercel/analytics @vercel/blob @vercel/functions
pnpm remove postgres redis
pnpm remove @prosemirror-*
pnpm remove react-data-grid tokenlens
```

#### 2. 优化 Next.js 配置

```typescript
// next.config.ts
import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // 保持开发环境友好，但生产环境严格
  eslint: {
    ignoreDuringBuilds: process.env.NODE_ENV === "development",
  },

  typescript: {
    ignoreBuildErrors: process.env.NODE_ENV === "development",
  },

  // 移除 PPR
  // experimental: {
  //   ppr: true,
  // },

  // 启用优化
  optimizePackageImports: [
    "@radix-ui/react-icons",
    "lucide-react",
    "date-fns",
  ],

  // 图片优化
  images: {
    formats: ["image/webp", "image/avif"],
  },

  // 压缩
  compress: true,

  // 生产环境优化
  ...(process.env.NODE_ENV === "production" && {
    output: "export",
    // 其他生产环境配置
  }),
};

export default nextConfig;
```

#### 3. 优化启动脚本

```json
{
  "scripts": {
    "dev": "next dev --port 3000",
    "dev:turbo": "next dev --turbo --port 3000",  // 可选
    "build": "next build",
    "analyze": "cross-env ANALYZE=true next build"
  }
}
```

## 性能监控

### 1. 使用 React DevTools

安装浏览器扩展：
- React Developer Tools
- React Profiler

### 2. Bundle 分析

```bash
# 分析打包大小
pnpm run analyze
```

### 3. 性能测试

```bash
# 使用 Lighthouse
npx lighthouse http://localhost:3000
```

## 常见性能问题及解决方案

### 1. 首屏加载慢

**原因**：
- 组件懒加载配置不当
- 静态资源未优化

**解决**：
```typescript
// 使用动态导入
const ChatPage = dynamic(() => import("./ChatPage"), {
  loading: () => <div>Loading...</div>,
  ssr: false
});

// 优化图片
import Image from "next/image";
<Image src="/logo.png" alt="Logo" width={100} height={100} priority />
```

### 2. 内存占用高

**原因**：
- 状态更新频繁
- 组件未卸载

**解决**：
```typescript
// 使用 React.memo
const Message = React.memo(({ message }) => {
  return <div>{message.content}</div>;
});

// 使用 useMemo
const expensiveValue = useMemo(() => {
  return computeExpensiveValue(data);
}, [data]);

// 使用 useCallback
const handleClick = useCallback(() => {
  doSomething();
}, []);
```

### 3. API 调用慢

**原因**：
- 重复请求
- 未使用缓存

**解决**：
```typescript
// 使用 SWR 缓存
import useSWR from "swr";

function useUser(id: string) {
  const { data, error, isLoading } = useSWR(`/api/users/${id}`, fetcher);
  return { user: data, error, isLoading };
}

// 请求去重
import { useSWRConfig } from "swr";

const config: useSWRConfig = {
  revalidateOnFocus: false,
  dedupingInterval: 5000,
};
```

## 推荐的生产环境配置

### 1. 使用稳定版本

```json
{
  "next": "14.2.13",
  "react": "^18.2.0"
}
```

### 2. 启用所有优化

```typescript
// next.config.ts
export default {
  compress: true,
  poweredByHeader: false,

  experimental: {
    optimizeCss: true,
    optimizePackageImports: true,
  },

  images: {
    formats: ["image/webp", "image/avif"],
  },

  webpack: (config) => {
    config.optimization.splitChunks = {
      chunks: "all",
      cacheGroups: {
        vendor: {
          test: /[\\/]node_modules[\\/]/,
          name: "vendors",
          chunks: "all",
        },
      },
    };
    return config;
  },
};
```

### 3. 部署优化

```dockerfile
# 使用多阶段构建
FROM node:18-alpine AS base
FROM node:18-alpine AS deps
FROM node:18-alpine AS builder
FROM node:18-alpine AS runner

# 复制产物
COPY --from=builder /app/.next /app/.next
COPY --from=deps /app/node_modules /app/node_modules

# 启动应用
CMD ["node", "server.js"]
```

## 监控工具推荐

1. **Sentry** - 错误监控
2. **LogRocket** - 用户会话回放
3. **Datadog** - 性能监控
4. **Vercel Analytics** - 基础分析

## 紧急修复步骤

如果应用特别慢，可以：

1. **重置到稳定状态**
```bash
rm -rf node_modules .next
cp package.optimized.json package.json
cp next.config.optimized.ts next.config.ts
pnpm install
pnpm dev
```

2. **临时禁用 Turbo 模式**
```bash
# 使用普通模式
pnpm dev --port 3000
```

3. **清理缓存**
```bash
pnpm exec next clean
rm -rf .next
```

---

更新时间：2025-01-16