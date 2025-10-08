// import { google } from "@ai-sdk/google";
// import { convertToModelMessages, streamText, type UIMessage } from "ai";

export const maxDuration = 30;

// process.env.sss = "ssss";

export async function POST(req: Request) {
  try {
    const body = await req.json();

    const userId = req.headers.get("x-user-id") ?? "1";
    const authHeader = req.headers.get("authorization");

    // 取出最后一条用户消息
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

    console.log("Forwarding to QA service:", { userId, question });

    // Build headers, including Authorization if present
    const headers: Record<string, string> = {
      "Content-Type": "application/json",
    };
    if (authHeader) {
      headers["Authorization"] = authHeader;
    }

    const resp = await fetch("http://localhost:8083/api/qa/ask", {
      method: "POST",
      headers,
      body: JSON.stringify({
        userId: Number(userId),
        question,
      }),
    });

    if (!resp.ok) {
      const text = await resp.text();
      console.error("QA service error:", text);
      return new Response(text || "后端错误", { status: resp.status });
    }

    const data = await resp.json();
    console.log("QA service response:", data);

    // Stream the message as expected by useChat
    const encoder = new TextEncoder();
    const stream = new ReadableStream({
      start(controller) {
        const message = {
          type: "message",
          id: crypto.randomUUID(),
          role: "assistant",
          content: [
            {
              type: "output_text",
              text: data.answer ?? "（后端未返回答案）",
            },
          ],
        };
        console.log("🧩 Stream message JSON:", JSON.stringify(message));
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
