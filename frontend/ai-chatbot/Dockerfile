# 构建阶段
FROM node:20-alpine AS builder
WORKDIR /app

# 安装 pnpm
RUN npm install -g pnpm

# 复制依赖文件并安装（确保依赖被正确安装）
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile

# 复制源代码
COPY . .

# 使用 pnpm 调用 next build（确保通过包管理器执行，避免路径问题）
RUN pnpm build

# 运行阶段
FROM node:20-alpine
WORKDIR /app
COPY --from=builder /app/next.config.js ./
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json

# 启动时执行迁移并启动服务
CMD ["sh", "-c", "pnpm db:migrate && next start"]