import { NextResponse } from "next/server";

export async function GET() {
  try {
    // 测试基本响应
    return NextResponse.json({
      message: "Frontend is running",
      timestamp: new Date().toISOString(),
      env: {
        NEXT_PUBLIC_GATEWAY_URL: process.env.NEXT_PUBLIC_GATEWAY_URL,
        NODE_ENV: process.env.NODE_ENV,
      }
    });
  } catch (error) {
    console.error("[Test] Error:", error);
    return NextResponse.json(
      { error: "Internal server error" },
      { status: 500 }
    );
  }
}