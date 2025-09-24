#!/bin/bash

echo "🚀 Starting AI Q&A System Development Environment"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Set AI API Key if provided
if [ -n "$AI_API_KEY" ]; then
    echo "✅ AI API Key detected"
    export AI_API_KEY=$AI_API_KEY
else
    echo "⚠️  No AI API Key provided. Set AI_API_KEY environment variable for full functionality."
fi

echo "📦 Building and starting services..."

# Start infrastructure services first
echo "🔧 Starting infrastructure (MySQL + Nacos)..."
docker-compose up -d mysql nacos

echo "⏳ Waiting for infrastructure to be ready..."
sleep 30

# Check if Nacos is ready
echo "🔍 Checking Nacos health..."
until curl -f http://localhost:8848/nacos/actuator/health > /dev/null 2>&1; do
    echo "Waiting for Nacos..."
    sleep 5
done

echo "✅ Infrastructure is ready!"

# Build backend services
echo "🔨 Building backend services..."
cd backend-services
mvn clean package -DskipTests
cd ..

# Start backend services
echo "🚀 Starting backend services..."
docker-compose up -d api-gateway user-service qa-service

echo "⏳ Waiting for backend services..."
sleep 20

# Start frontend
echo "🎨 Starting frontend..."
cd frontend
if [ ! -d "node_modules" ]; then
    echo "📦 Installing frontend dependencies..."
    npm install
fi

npm run dev &
FRONTEND_PID=$!
cd ..

echo ""
echo "🎉 AI Q&A System is starting up!"
echo ""
echo "📍 Service URLs:"
echo "   Frontend:     http://localhost:3000"
echo "   API Gateway:  http://localhost:8080"
echo "   Nacos:        http://localhost:8848/nacos (user: nacos, password: nacos)"
echo ""
echo "⏳ Services are initializing... Please wait 1-2 minutes for full startup."
echo ""
echo "To stop all services, run: docker-compose down && kill $FRONTEND_PID"

# Keep script running
wait $FRONTEND_PID