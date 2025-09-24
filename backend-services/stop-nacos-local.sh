#!/bin/bash

# 停止本地Nacos服务器（二进制方式）

echo "🛑 停止本地Nacos服务器..."

NACOS_DIR="nacos"

if [ ! -d "$NACOS_DIR" ]; then
    echo "❌ Nacos目录不存在"
    exit 1
fi

# 进入Nacos目录并停止
cd $NACOS_DIR/bin

if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    # Windows
    ./shutdown.cmd
else
    # Unix-like (Linux, macOS)
    chmod +x shutdown.sh
    ./shutdown.sh
fi

cd ../..

# 等待进程停止
echo "⏳ 等待进程停止..."
sleep 3

# 检查是否还有进程在运行
if lsof -i :8848 >/dev/null 2>&1; then
    echo "⚠️  强制停止残留进程..."
    # 查找并杀死Nacos相关进程
    pids=$(ps aux | grep nacos | grep -v grep | awk '{print $2}')
    if [ -n "$pids" ]; then
        echo $pids | xargs kill -9
    fi
fi

echo "✅ Nacos服务器已停止"
