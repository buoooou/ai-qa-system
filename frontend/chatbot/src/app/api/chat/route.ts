import { NextResponse } from "next/server";

type ChatRequestBody = {
  message: string;
  userId?: string;
  sessionId?: string;
};

export async function POST(request: Request) {
  try {
    const body = (await request.json()) as ChatRequestBody;
    const userMessage = body.message?.toString() ?? "";
    const userId = (body.userId ?? process.env.NEXT_PUBLIC_DEFAULT_USER_ID ?? "1").toString();
    const sessionId = (body.sessionId ?? "").toString();

    if (!userMessage) {
      return NextResponse.json({ error: "message is required" }, { status: 400 });
    }

    const backendBaseUrl = process.env.BACKEND_BASE_URL ?? "http://localhost:8080"; // 通过 API Gateway 访问

    // 1) 使用 Google AI 获取回答
    const answerResponse = await fetch(`${backendBaseUrl}/api/qa/ask`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        question: userMessage,
        userId: userId,
      }),
    });

    if (!answerResponse.ok) {
      const text = await answerResponse.text();
      return NextResponse.json(
        { error: "failed to get answer", details: text },
        { status: 502 },
      );
    }

    const answerData = await answerResponse.json();
    const answerText = answerData.answer || answerData.error || "抱歉，我无法回答您的问题。";
    
    // 如果后端返回错误信息，我们仍然返回给前端显示
    // 这样用户可以看到后端的状态信息

    // 2) 保存问答历史（如果后端可用）
    try {
      await fetch(`${backendBaseUrl}/api/qa/save`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          userId,
          question: userMessage,
          answer: answerText,
          sessionId,
        }),
      });
      // 忽略保存失败的错误，不阻断前端显示回答
    } catch {
      // noop
    }

    return NextResponse.json({
      answer: answerText,
    });
  } catch (error) {
    return NextResponse.json({ error: (error as Error).message }, { status: 500 });
  }
}


