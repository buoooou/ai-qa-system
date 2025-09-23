import { NextResponse } from 'next/server';

/**
 * 健康检查API端点
 * 用于Docker容器健康检查和负载均衡器探测
 */
export async function GET() {
  try {
    // 检查应用基本状态
    const healthStatus = {
      status: 'healthy',
      timestamp: new Date().toISOString(),
      service: 'ai-qa-frontend',
      version: process.env.npm_package_version || '1.0.0',
      environment: process.env.NODE_ENV || 'production',
      uptime: process.uptime(),
    };

    return NextResponse.json(healthStatus, { status: 200 });
  } catch (error) {
    // 如果出现错误，返回不健康状态
    const errorStatus = {
      status: 'unhealthy',
      timestamp: new Date().toISOString(),
      service: 'ai-qa-frontend',
      error: error instanceof Error ? error.message : 'Unknown error',
    };

    return NextResponse.json(errorStatus, { status: 503 });
  }
}