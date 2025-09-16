#!/bin/bash

# AI智能问答系统启动脚本
echo "🚀 启动AI智能问答系统..."

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker未安装，请先安装Docker"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose未安装，请先安装Docker Compose"
    exit 1
fi

# 检查端口是否被占用
check_port() {
    local port=$1
    local service=$2
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        echo "⚠️  端口 $port 被占用，请关闭占用 $service 端口的进程"
        return 1
    fi
    return 0
}

echo "🔍 检查端口占用情况..."
check_port 3000 "前端应用" || exit 1
check_port 8080 "API网关" || exit 1
check_port 8081 "用户服务" || exit 1
check_port 8082 "问答服务" || exit 1
check_port 3306 "MySQL数据库" || exit 1

# 检查环境变量
if [ -z "$GEMINI_API_KEY" ]; then
    echo "⚠️  未设置GEMINI_API_KEY环境变量"
    echo "请设置Gemini API Key: export GEMINI_API_KEY=your_api_key"
    echo "或者在docker-compose.yml中手动配置"
fi

# 构建并启动服务
echo "🏗️  构建Docker镜像..."
docker-compose build

echo "🚀 启动所有服务..."
docker-compose up -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 30

# 检查服务状态
echo "🔍 检查服务状态..."
docker-compose ps

# 健康检查
echo "🏥 执行健康检查..."

check_service() {
    local url=$1
    local service=$2
    local max_attempts=10
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f -s $url > /dev/null; then
            echo "✅ $service 服务正常"
            return 0
        fi
        echo "⏳ 等待 $service 服务启动... (尝试 $attempt/$max_attempts)"
        sleep 5
        ((attempt++))
    done
    
    echo "❌ $service 服务启动失败"
    return 1
}

# 检查各个服务
check_service "http://localhost:8081/health" "用户服务"
check_service "http://localhost:8082/health" "问答服务"
check_service "http://localhost:8080/actuator/health" "API网关"
check_service "http://localhost:3000" "前端应用"

echo ""
echo "🎉 AI智能问答系统启动完成！"
echo ""
echo "📱 访问地址："
echo "   前端应用: http://localhost:3000"
echo "   API网关:  http://localhost:8080"
echo "   用户服务: http://localhost:8081"
echo "   问答服务: http://localhost:8082"
echo ""
echo "📋 常用命令："
echo "   查看日志: docker-compose logs -f"
echo "   停止服务: docker-compose down"
echo "   重启服务: docker-compose restart"
echo ""
echo "🔧 如果遇到问题，请查看日志或README.md文档"
