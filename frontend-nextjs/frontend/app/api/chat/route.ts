import { google } from "@ai-sdk/google"
import { convertToModelMessages, streamText, type UIMessage } from "ai"
import { NextRequest } from "next/server";

export const maxDuration = 30

// process.env.sss="ssss"

export async function POST(req: NextRequest) {
  try {
    const { messages }: { messages: UIMessage[] } = await req.json();

    if (!messages || !Array.isArray(messages) || messages.length === 0) {
      return new Response("Messages are required", { status: 400 });
    }

    const prompt = convertToModelMessages(messages);

    const result = streamText({
      model: google("gemini-2.0-flash"),
      messages: prompt,
      abortSignal: req.signal,
      // maxTokens: 2000,
      temperature: 0.7,
    });

    return result.toUIMessageStreamResponse({
      onFinish: async ({ isAborted }) => {
        if (isAborted) {
          console.log("Chat request aborted");
        }
      },
    });
  } catch (error) {
    console.error("Chat API error:", error);
    return new Response("Internal server error", { status: 500 });
  }
}
