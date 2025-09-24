#!/bin/bash

# AI问答系统快速启动脚本（使用公网Nacos）

echo "🚀 启动AI问答系统（公网Nacos模式）..."

# 检查MySQL是否运行
if ! pgrep -x "mysqld" > /dev/null; then
    echo "⚠️  MySQL未运行，请先启动MySQL服务"
    echo "   macOS: brew services start mysql"
    echo "   或者: sudo /usr/local/mysql/support-files/mysql.server start"
    exit 1
fi

# 设置环境变量
export AI_API_KEY=${AI_API_KEY:-"your-api-key-here"}

echo "📊 当前配置:"
echo "   - Nacos服务器: nacos.io:8848 (公网测试服务器)"
echo "   - MySQL: localhost:3306"
echo "   - 用户服务端口: 8081"
echo "   - 问答服务端口: 8082"
echo "   - API网关端口: 8080"

# 测试公网Nacos连接
echo "🔗 测试公网Nacos连接..."
if curl -s --connect-timeout 5 http://nacos.io:8848/nacos >/dev/null 2>&1; then
    echo "✅ 公网Nacos连接正常"
else
    echo "❌ 无法连接到公网Nacos服务器"
    echo "   请检查网络连接或使用本地Nacos: ./start-nacos-local.sh"
    exit 1
fi

# 构建所有服务
echo "🔨 构建项目..."
mvn clean compile -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 构建失败，请检查代码"
    exit 1
fi

echo "✅ 构建成功！"

# 创建日志目录
mkdir -p logs

# 启动服务的函数
start_service() {
    local service_name=$1
    local service_path=$2
    local port=$3

    echo "🚀 启动 $service_name (端口: $port)..."
    cd $service_path
    nohup mvn spring-boot:run > ../logs/${service_name}.log 2>&1 &
    local pid=$!
    echo $pid > ../logs/${service_name}.pid
    cd ..

    # 等待服务启动并注册到Nacos
    echo "⏳ 等待 $service_name 启动并注册到Nacos..."
    sleep 15

    if ps -p $pid > /dev/null; then
        echo "✅ $service_name 启动成功 (PID: $pid)"
        return 0
    else
        echo "❌ $service_name 启动失败，查看日志: logs/${service_name}.log"
        return 1
    fi
}

# 按顺序启动服务
echo "📋 启动服务..."

# 1. 启动用户服务
start_service "用户服务" "user-service" "8081"
if [ $? -ne 0 ]; then
    echo "❌ 用户服务启动失败，终止启动"
    exit 1
fi

# 2. 启动问答服务
start_service "问答服务" "qa-service" "8082"
if [ $? -ne 0 ]; then
    echo "❌ 问答服务启动失败，终止启动"
    exit 1
fi

# 3. 启动API网关（需要更长时间等待服务发现）
echo "🚀 启动API网关 (端口: 8080)..."
cd api-gateway
nohup mvn spring-boot:run > ../logs/API网关.log 2>&1 &
local gateway_pid=$!
echo $gateway_pid > ../logs/API网关.pid
cd ..

echo "⏳ 等待API网关启动并发现服务..."
sleep 20

if ps -p $gateway_pid > /dev/null; then
    echo "✅ API网关启动成功 (PID: $gateway_pid)"
else
    echo "❌ API网关启动失败，查看日志: logs/API网关.log"
    exit 1
fi

echo ""
echo "🎉 所有服务启动完成！"
echo ""
echo "📱 访问地址:"
echo "   - API网关: http://localhost:8080"
echo "   - 用户服务: http://localhost:8081"
echo "   - 问答服务: http://localhost:8082"
echo "   - Swagger文档: http://localhost:8081/swagger-ui/"
echo "   - Nacos控制台: http://nacos.io:8848/nacos (用户名/密码: nacos/nacos)"
echo ""
echo "📝 查看日志:"
echo "   - 用户服务: tail -f logs/用户服务.log"
echo "   - 问答服务: tail -f logs/问答服务.log"
echo "   - API网关: tail -f logs/API网关.log"
echo ""
echo "🛑 停止服务: ./stop-services.sh"
echo ""
echo "💡 提示: 服务注册到Nacos可能需要几分钟，请耐心等待"
