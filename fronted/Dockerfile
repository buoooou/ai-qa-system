# 构建阶段
FROM node:18 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install --unsafe-perm  # 忽略权限问题
COPY . .
RUN npm run build

# 运行时镜像
FROM nginx:alpine
COPY --from=build /app/out /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
