#!/bin/bash

# AI智能问答系统环境变量设置脚本
echo "🚀 AI智能问答系统环境变量设置"
echo "=================================="

# 检查.env文件是否存在
if [ ! -f ".env" ]; then
    echo "📋 创建.env文件..."
    cp .env.example .env
    echo "✅ .env文件已创建，请编辑其中的配置"
else
    echo "✅ .env文件已存在"
fi

# 检查GEMINI_API_KEY是否设置
if grep -q "GEMINI_API_KEY=your_gemini_api_key_here" .env; then
    echo ""
    echo "⚠️  请设置您的Gemini API Key："
    echo "   1. 访问 https://makersuite.google.com/app/apikey"
    echo "   2. 创建新的API Key"
    echo "   3. 编辑.env文件，替换 'your_gemini_api_key_here' 为您的真实API Key"
    echo ""
    read -p "是否现在设置API Key? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        read -p "请输入您的Gemini API Key: " api_key
        if [ ! -z "$api_key" ]; then
            # 在macOS和Linux上都能工作的sed命令
            if [[ "$OSTYPE" == "darwin"* ]]; then
                # macOS
                sed -i '' "s/GEMINI_API_KEY=your_gemini_api_key_here/GEMINI_API_KEY=$api_key/" .env
            else
                # Linux
                sed -i "s/GEMINI_API_KEY=your_gemini_api_key_here/GEMINI_API_KEY=$api_key/" .env
            fi
            echo "✅ API Key已设置"
        fi
    fi
fi

echo ""
echo "🔧 环境变量配置完成！"
echo ""
echo "📝 使用方法："
echo "   1. Docker Compose: docker-compose up -d"
echo "   2. 本地开发: source .env && mvn spring-boot:run"
echo ""
echo "🔍 验证配置："
echo "   docker-compose config"
echo ""
