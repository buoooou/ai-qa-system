#!/bin/bash

echo "=== 启动AI Q&A系统服务 ==="

# 启动Nacos (如果未启动)
echo "检查Nacos状态..."
if ! curl -s http://localhost:8848/nacos/actuator/health > /dev/null; then
    echo "启动Nacos..."
    cd ~/nacos/bin && sh startup.sh -m standalone &
    sleep 10
else
    echo "Nacos已在运行"
fi

# 启动qa-service (端口8082)
echo "启动qa-service..."
cd /Users/leo/Documents/GitHub/ai-qa-system/backend-services/qa-service
mvn spring-boot:run &
QA_PID=$!

# 等待qa-service启动
sleep 20

# 启动user-service (端口8081)
echo "启动user-service..."
cd /Users/leo/Documents/GitHub/ai-qa-system/backend-services/user-service
mvn spring-boot:run &
USER_PID=$!

# 等待user-service启动
sleep 20

# 启动api-gateway (端口8080)
echo "启动api-gateway..."
cd /Users/leo/Documents/GitHub/ai-qa-system/backend-services/api-gateway
mvn spring-boot:run &
GATEWAY_PID=$!

echo "=== 服务启动完成 ==="
echo "Nacos控制台: http://localhost:8848/nacos (nacos/nacos)"
echo "API网关: http://localhost:8080"
echo "QA服务: http://localhost:8082"
echo "用户服务: http://localhost:8081"
echo ""
echo "进程IDs:"
echo "QA Service: $QA_PID"
echo "User Service: $USER_PID"
echo "Gateway: $GATEWAY_PID"
echo ""
echo "按Ctrl+C停止所有服务"

# 等待用户中断
wait