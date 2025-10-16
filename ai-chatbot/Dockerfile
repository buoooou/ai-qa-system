# === Build stage ===
FROM node:20-alpine AS build

WORKDIR /app

# 启用 pnpm
ENV PNPM_HOME=/usr/local/share/pnpm
ENV PATH="${PNPM_HOME}:${PATH}"
RUN corepack enable

# 拷贝依赖清单
COPY package.json pnpm-lock.yaml ./

# 安装依赖（包括dev依赖）
RUN pnpm install --frozen-lockfile

# 拷贝所有源代码
COPY . .

# 关键点：Next.js 构建
RUN pnpm run build

# 清理掉 devDependencies
RUN pnpm prune --prod


# === Runtime stage ===
FROM node:20-alpine AS runner

WORKDIR /app
ENV NODE_ENV=production

# 拷贝 Next.js 产物（standalone 模式）
COPY --from=build /app/.next/standalone ./
COPY --from=build /app/.next/static ./.next/static
COPY --from=build /app/public ./public

# 暴露端口
EXPOSE 3000

# 启动命令（standalone 模式的入口文件）
CMD ["node", "server.js"]
