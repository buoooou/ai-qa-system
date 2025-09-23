# 构建阶段
FROM node:20-alpine AS builder
WORKDIR /app

# 关键修改1：定义构建参数，用于构建阶段
ARG POSTGRES_URL
ENV POSTGRES_URL=$POSTGRES_URL

# 复制依赖文件
COPY package.json pnpm-lock.yaml ./
RUN npm install -g pnpm && pnpm install

# 复制源代码
COPY . .

# 执行构建（此时需要POSTGRES_URL）
RUN pnpm build

# 运行阶段
FROM node:20-alpine
WORKDIR /app

# 复制构建产物
COPY --from=builder /app/package.json ./
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/lib ./lib

# 关键修改2：运行阶段也需要这个变量（迁移和应用启动时用）
ENV POSTGRES_URL=$POSTGRES_URL

# 暴露端口
EXPOSE 3000

# 创建启动脚本：先迁移再启动
RUN echo "#!/bin/sh" > /app/start.sh && \
    echo "set -e" >> /app/start.sh && \
    echo "pnpm db:migrate" >> /app/start.sh && \
    echo "pnpm start" >> /app/start.sh && \
    chmod +x /app/start.sh

# 启动命令
CMD ["/app/start.sh"]
    