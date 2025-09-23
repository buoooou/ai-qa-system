# 构建阶段
FROM node:20-alpine AS builder
WORKDIR /app

# 复制依赖文件
COPY package.json pnpm-lock.yaml ./
RUN npm install -g pnpm && pnpm install

# 复制源代码
COPY . .

# 关键修改：添加POSTGRES_URL环境变量（构建阶段需要）
# 注意：这里使用构建参数，实际值在构建时传入
ARG POSTGRES_URL
ENV POSTGRES_URL=$POSTGRES_URL

# 执行数据库迁移（现在可以获取到POSTGRES_URL）
RUN pnpm db:migrate

# 构建前端应用
RUN pnpm build

# 运行阶段
FROM node:20-alpine
WORKDIR /app

# 复制构建产物
COPY --from=builder /app/package.json ./
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/node_modules ./node_modules

# 暴露端口
EXPOSE 3000

# 启动命令
CMD ["pnpm", "start"]
    