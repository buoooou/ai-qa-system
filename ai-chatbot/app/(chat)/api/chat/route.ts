import { createUIMessageStream, JsonToSseTransformStream } from "ai";
import { headers } from "next/headers";

import { auth } from "@/app/(auth)/auth";
import { streamGatewayChat } from "@/lib/api/gateway";
import { ChatSDKError } from "@/lib/errors";
import type { ChatMessage } from "@/lib/types";
import { type PostRequestBody, postRequestBodySchema } from "./schema";

export const maxDuration = 60;

export async function POST(request: Request) {
  let requestBody: PostRequestBody;

  try {
    const json = await request.json();
    requestBody = postRequestBodySchema.parse(json);
  } catch (_) {
    return new ChatSDKError("bad_request:api").toResponse();
  }

  try {
    const session = await auth();

    if (!session?.user) {
      return new ChatSDKError("unauthorized:chat").toResponse();
    }

    if (!session.user.accessToken) {
      console.error("No accessToken in session", { session });
      return new ChatSDKError("unauthorized:chat", "Please log in again").toResponse();
    }

    const forwardedFor = (await headers()).get("x-forwarded-for") ?? undefined;

    const response = await streamGatewayChat(
      {
        ...requestBody,
        userId: Number.parseInt(session.user.id, 10),
        clientIp: forwardedFor,
      },
      session.user.accessToken
    );

    // Transform Gateway SSE to AI SDK format
    const uiMessageStream = createUIMessageStream<ChatMessage>({
      execute: async ({ writer }) => {
        const reader = response.data.getReader();
        const decoder = new TextDecoder();
        let buffer = '';
        let messageId = requestBody.message.id;
        let currentMessage: ChatMessage = {
          id: messageId,
          role: 'assistant',
          parts: [{ type: 'text', text: '' }]
        };

        try {
          while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            buffer += decoder.decode(value, { stream: true });
            const lines = buffer.split('\n');
            buffer = lines.pop() || '';

            for (const line of lines) {
              if (line.startsWith('data: ')) {
                const dataStr = line.slice(6).trim();
                if (!dataStr) continue;

                try {
                  const data = JSON.parse(dataStr);
                  
                  if (data.type === 'content' && data.text) {
                    // Append content to message
                    if (currentMessage.parts[0].type === 'text') {
                      currentMessage.parts[0].text += data.text;
                    }
                    
                    // Send incremental update
                    writer.write({
                      type: 'data-textDelta',
                      data: data.text,
                    });
                  } else if (data.type === 'end') {
                    // Mark message as complete
                    writer.write({
                      type: 'finish',
                    });
                  }
                } catch (e) {
                  console.error('Failed to parse SSE data:', e, dataStr);
                }
              }
            }
          }
        } catch (error) {
          console.error('Stream reading error:', error);
          writer.write({
            type: 'error',
            errorText: error instanceof Error ? error.message : 'Stream error',
          });
        } finally {
          reader.releaseLock();
        }
      },
    });

    return new Response(
      uiMessageStream.pipeThrough(new JsonToSseTransformStream()),
      {
        headers: {
          "Content-Type": "text/event-stream",
          "Cache-Control": "no-cache",
          "Connection": "keep-alive",
        },
      }
    );
  } catch (error) {
    console.error(error);

    if (error instanceof ChatSDKError) {
      return error.toResponse();
    }

    if (
      error instanceof Error &&
      error.message?.includes(
        "AI Gateway requires a valid credit card on file to service requests"
      )
    ) {
      return new ChatSDKError("bad_request:activate_gateway").toResponse();
    }

    return new ChatSDKError("offline:chat").toResponse();
  }
}

// DELETE handler will be reimplemented via gateway once backend endpoint exists.
