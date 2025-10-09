// app/api/chat/route.ts
import { NextRequest } from "next/server";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8083";

export async function POST(req: NextRequest) {
  try {
    const body = await req.json();
    const userId = req.headers.get("x-user-id") ?? "1";
    const authHeader = req.headers.get("authorization");

    const messages = body.messages;
    const lastMessage = messages?.[messages.length - 1];
    let question = "";

    if (lastMessage?.parts?.[0]?.type === "text") {
      question = lastMessage.parts[0].text;
    }

    if (!question) {
      return new Response(JSON.stringify({ error: "缺少问题内容" }), {
        status: 400,
      });
    }

    // 调用 Spring Boot QA 接口
    const resp = await fetch(`${API_BASE_URL}/api/qa/ask`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        ...(authHeader ? { Authorization: authHeader } : {}),
      },
      body: JSON.stringify({
        userId: Number(userId),
        question,
      }),
    });

    if (!resp.ok) {
      const text = await resp.text();
      return new Response(text || "后端错误", { status: resp.status });
    }

    const data = await resp.json();

    const encoder = new TextEncoder();
    const stream = new ReadableStream({
      start(controller) {
        const message = {
          type: "message",
          message: {
            id: crypto.randomUUID(),
            role: "assistant",
            parts: [
              { type: "text", text: data.answer ?? "（后端未返回答案）" },
            ],
          },
        };
        controller.enqueue(encoder.encode(JSON.stringify(message) + "\n"));
        controller.close();
      },
    });

    return new Response(stream, {
      headers: { "Content-Type": "application/x-ndjson" },
      status: 200,
    });
  } catch (err) {
    return new Response(JSON.stringify({ error: (err as Error).message }), {
      status: 500,
    });
  }
}
