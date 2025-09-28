import type { ChatTransport, Message } from "@ai-sdk/react";
import { useSession } from "next-auth/react";
import { createUserSession } from "@/lib/api/gateway";
import { ChatSDKError } from "@/lib/errors";

interface GatewayChatRequest {
  id: string;
  message: Message;
  selectedChatModel: string;
  selectedVisibilityType: string;
  sessionId?: string;
  sessionTitle?: string;
  history?: Array<{ role: "user" | "assistant"; content: string }>;
}

export class GatewayChatTransport implements ChatTransport {
  private baseUrl: string;
  private accessToken: string;

  constructor(baseUrl = "/api/chat", accessToken: string) {
    this.baseUrl = baseUrl;
    this.accessToken = accessToken;
  }

  async stream(request: GatewayChatRequest): Promise<ReadableStream<string>> {
    // Create session if not exists
    let sessionId = request.sessionId;
    if (!sessionId && request.sessionTitle) {
      try {
        const session = await createUserSession(
          { title: request.sessionTitle },
          { accessToken: this.accessToken }
        );
        sessionId = session.id.toString();
      } catch (error) {
        console.error("Failed to create session:", error);
        throw new ChatSDKError("failed:session_creation");
      }
    }

    const response = await fetch(this.baseUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${this.accessToken}`,
      },
      body: JSON.stringify({
        ...request,
        sessionId,
      }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`HTTP ${response.status}: ${errorText}`);
    }

    if (!response.body) {
      throw new Error("No response body");
    }

    return response.body
      .pipeThrough(new TextDecoderStream())
      .pipeThrough(new TransformStream({
        transform(chunk, controller) {
          // Parse SSE chunks
          const lines = chunk.split('\n');
          for (const line of lines) {
            if (line.startsWith('data: ')) {
              const data = line.slice(6).trim();
              if (data && data !== '[DONE]') {
                controller.enqueue(data);
              }
            }
          }
        }
      }));
  }
}
