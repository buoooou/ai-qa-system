#!/bin/bash

# AI QA System 部署脚本
# 使用方法: ./deploy.sh [environment]
# 环境: dev, prod

set -e  # 遇到错误立即退出

ENVIRONMENT=${1:-dev}
SERVER_HOST="3.84.225.222"
SERVER_USER="ubuntu"  # 根据实际情况修改
PROJECT_DIR="/home/ubuntu/ai-qa-system"  # 根据实际情况修改

echo "🚀 开始部署到 $ENVIRONMENT 环境..."

# 检查参数
if [[ "$ENVIRONMENT" != "dev" && "$ENVIRONMENT" != "prod" ]]; then
    echo "❌ 错误: 环境参数必须是 'dev' 或 'prod'"
    exit 1
fi

echo "📋 部署信息:"
echo "  - 环境: $ENVIRONMENT"
echo "  - 服务器: $SERVER_HOST"
echo "  - 项目目录: $PROJECT_DIR"

# 部署到服务器
echo "🔧 在服务器上执行部署..."

ssh -o StrictHostKeyChecking=no $SERVER_USER@$SERVER_HOST << EOF
    set -e
    echo "📁 进入项目目录: $PROJECT_DIR"
    cd $PROJECT_DIR

    echo "📥 拉取最新代码..."
    git fetch origin
    git checkout feature/yulong
    git pull origin feature/yulong

    echo "🐳 停止现有容器..."
    docker-compose down || true

    echo "🔨 构建并启动新容器..."
    docker-compose up -d --build

    echo "⏳ 等待服务启动..."
    sleep 30

    echo "🏥 检查服务状态..."
    docker-compose ps

    echo "✅ 部署完成!"
EOF

echo "🎉 部署成功完成!"
echo "🌐 前端访问地址: http://$SERVER_HOST:3000"
echo "🔗 API网关地址: http://$SERVER_HOST:8080"
