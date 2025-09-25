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
    echo "📁 检查项目目录: $PROJECT_DIR"
    
    # 检查项目目录是否存在
    if [ ! -d "$PROJECT_DIR" ]; then
        echo "📂 项目目录不存在，正在创建..."
        mkdir -p $PROJECT_DIR
        cd $PROJECT_DIR
        
        echo "📥 克隆项目仓库..."
        git clone https://github.com/pzone618/ai-qa-system.git .
        git checkout feature/yulong
    else
        echo "📁 进入项目目录: $PROJECT_DIR"
        cd $PROJECT_DIR
        
        echo "📥 拉取最新代码..."
        git fetch origin
        git checkout feature/yulong
        git pull origin feature/yulong
    fi

    echo "🔍 检查Docker和Docker Compose..."
    if ! command -v docker &> /dev/null; then
        echo "❌ Docker未安装，请先安装Docker"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        echo "❌ Docker Compose未安装，请先安装Docker Compose"
        exit 1
    fi
    
    echo "🐳 停止现有容器..."
    docker-compose down || true
    
    echo "🔨 构建并启动新容器..."
    docker-compose up -d --build
    
    echo "⏳ 等待服务启动..."
    sleep 30
    
    echo "🏥 检查服务状态..."
    docker-compose ps
    
    echo "🌐 检查端口占用..."
    netstat -tlnp | grep -E ':(3000|8080|8081|8082|5432|6379)' || echo "⚠️  无法检查端口状态"
    
    echo "✅ 部署完成!"
EOF

echo "🎉 部署成功完成!"
echo "🌐 前端访问地址: http://$SERVER_HOST:3000"
echo "🔗 API网关地址: http://$SERVER_HOST:8080"
