import { NextResponse } from "next/server";
import { gatewayClient } from "@/lib/api/gateway";

export async function GET() {
  try {
    console.log("[Test-Backend] Testing backend connection...");

    // 测试连接到后端
    const response = await gatewayClient.get("/api/gateway/public/health", {
      timeout: 5000,
    });

    console.log("[Test-Backend] Backend response:", response.data);

    return NextResponse.json({
      message: "Backend connection successful",
      data: response.data,
      baseURL: gatewayClient.defaults.baseURL,
    });
  } catch (error) {
    console.error("[Test-Backend] Backend connection error:", error);
    return NextResponse.json(
      {
        error: "Backend connection failed",
        details: error.message,
        baseURL: gatewayClient.defaults.baseURL,
      },
      { status: 500 }
    );
  }
}