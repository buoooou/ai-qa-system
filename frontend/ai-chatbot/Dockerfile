# 构建阶段
FROM node:20-alpine AS builder
WORKDIR /app
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile
COPY . .

# 移除构建阶段的数据库迁移步骤，只执行 next build
RUN next build  # 这里只构建应用，不执行迁移

# 运行阶段
FROM node:20-alpine
WORKDIR /app
COPY --from=builder /app/next.config.js ./
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json

# 添加启动脚本，在容器启动时执行迁移
CMD ["sh", "-c", "pnpm db:migrate && next start"]