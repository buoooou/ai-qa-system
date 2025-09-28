#!/bin/bash

# 快速部署脚本
set -e

echo "🚀 开始快速部署..."

# 停止现有容器
echo "🛑 停止现有容器..."
docker-compose down || true

# 清理 Docker 缓存
echo "🧹 清理 Docker 缓存..."
docker system prune -f

# 构建并启动所有服务
echo "🔨 构建并启动所有服务..."
docker-compose up -d --build

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 90

# 检查服务状态
echo "🏥 检查服务状态..."
docker-compose ps

# 检查端口状态
echo "🌐 检查端口状态..."
ss -tlnp | grep -E ':(3000|8080|8081|8082|5432|6379)' || echo "⚠️  端口检查失败"

# 健康检查
echo "🔍 执行健康检查..."
sleep 10

# 检查前端
if curl -f http://localhost:3000/login/ > /dev/null 2>&1; then
  echo "✅ 前端服务正常"
else
  echo "❌ 前端服务异常"
  docker-compose logs frontend
fi

# 检查 API Gateway
if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
  echo "✅ API Gateway 正常"
else
  echo "❌ API Gateway 异常"
  docker-compose logs api-gateway
fi

echo "✅ 部署完成！"
echo "🌐 前端访问地址: http://54.234.25.135:3000"
echo "🔗 API Gateway: http://54.234.25.135:8080"
