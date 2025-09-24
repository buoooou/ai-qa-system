# 构建阶段
FROM node:18 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install --unsafe-perm  # 忽略权限问题
COPY . .
RUN npm run dev

# 生产阶段
FROM node:18 AS production
WORKDIR /app
COPY --from=build /app/package*.json ./
COPY --from=build /app/node_modules ./node_modules
COPY --from=build /app/.next ./.next
COPY --from=build /app/public ./public

# 启动命令
EXPOSE 3000
CMD ["npm", "start"]


# 运行时镜像
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
