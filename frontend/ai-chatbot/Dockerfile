# 构建阶段
FROM node:20-alpine AS builder
WORKDIR /app

# 安装 pnpm
RUN npm install -g pnpm

# 复制依赖文件并安装
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile

# 复制源代码
COPY . .

# 构建阶段注入 POSTGRES_URL 占位符（避免迁移脚本检查失败）
# 注意：实际数据库连接在运行时使用真实环境变量
ENV POSTGRES_URL="postgresql://aiqa_front:aiqa_front123@postgres:5432/ai_qa_frontend"

# 仅执行 Next.js 构建，不执行迁移
RUN pnpm build

# 运行阶段
FROM node:20-alpine
WORKDIR /app
COPY --from=builder /app/next.config.ts ./
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./package.json

# 启动时先执行迁移，再启动服务（此时已能访问数据库）
CMD ["sh", "-c", "pnpm db:migrate && next start"]