# 构建阶段
FROM node:18 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install --unsafe-perm  # 忽略权限问题
COPY . .
RUN npm run build

# 运行时镜像
FROM node:18-alpine
WORKDIR /app
COPY --from=build /app/package*.json ./
COPY --from=build /app/node_modules ./node_modules
COPY --from=build /app/.next ./.next
COPY --from=build /app/public ./public
COPY --from=build /app/next.config.ts ./
EXPOSE 3000
CMD ["npm", "start"]
