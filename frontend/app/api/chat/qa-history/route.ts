import { NextResponse } from "next/server";

const BACKEND_API_URL =
  process.env.BACKEND_QA_API_URL || "http://localhost:8080";

// 获取会话历史
export async function POST(req: Request) {
  const { sessionId, userid, topic, specifySId } = await req.json();
  try {
    const response = await fetch(
      `${BACKEND_API_URL}/api/qa/sessions/qahistory`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ sessionId, userid, topic, specifySId }),
      }
    );
    const data = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    console.error("Logout error:", error);
    return NextResponse.json(
      { error: "Failed to get history session" },
      { status: 500 }
    );
  }
}
