import { NextResponse } from "next/server";

const BACKEND_API_URL =
  process.env.BACKEND_QA_API_URL || "http://localhost:8080";

// 发送消息并获取 AI 回复
export async function POST(req: Request) {
  const { sessionId, userId, question } = await req.json();
  try {
    const response = await fetch(`${BACKEND_API_URL}/api/qa/messages`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ sessionId, userId, question }),
    });

    // 兼容纯文本和 JSON 响应
    const contentType = response.headers.get("Content-Type") || "";
    let data;
    if (contentType.includes("application/json")) {
      data = await response.json();
    } else {
      data = await response.text();
    }

    return NextResponse.json({
      status: response.status,
      content: data,
    });
  } catch (error) {
    console.error("Logout error:", error);
    return NextResponse.json(
      { error: "Failed to send message" },
      { status: 500 }
    );
  }
}
