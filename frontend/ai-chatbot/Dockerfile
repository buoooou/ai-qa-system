# 构建阶段
FROM node:20-alpine AS builder
WORKDIR /app

# 复制依赖文件
COPY package.json pnpm-lock.yaml ./
RUN npm install -g pnpm && pnpm install

# 复制源代码
COPY . .

# 移除构建阶段的数据库迁移步骤（关键修改）
# 只执行构建，不执行迁移
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

# 暴露端口
EXPOSE 3000

# 关键修改：创建启动脚本，先执行迁移再启动应用
RUN echo "#!/bin/sh" > /app/start.sh && \
    echo "pnpm db:migrate" >> /app/start.sh && \
    echo "pnpm start" >> /app/start.sh && \
    chmod +x /app/start.sh

# 使用启动脚本执行迁移并启动应用
CMD ["/app/start.sh"]
    