import { createUIMessageStream, JsonToSseTransformStream } from "ai";
import { headers } from "next/headers";
import { Readable } from "stream";

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

    // Debug: Log response structure
    console.log("[CHAT] Gateway response type:", typeof response.data);
    console.log("[CHAT] Response data constructor:", response.data?.constructor?.name);
    console.log("[CHAT] Has getReader method:", typeof response.data?.getReader === 'function');

      // Transform backend SSE format to standard SSE format that AI SDK can understand
    const encoder = new TextEncoder();
    const decoder = new TextDecoder();
    let messageId = requestBody.message.id;
    let hasStarted = false;
    let accumulatedText = '';

    const transformedStream = new ReadableStream({
      async start(controller) {
        let stream: ReadableStream<Uint8Array>;

        if (response.data instanceof ReadableStream) {
          stream = response.data;
        } else if (response.data && typeof response.data === 'object' && 'pipe' in response.data) {
          stream = Readable.fromWeb(response.data as any);
        } else {
          controller.error(new Error('Invalid stream type'));
          return;
        }

        const reader = stream.getReader();
        let buffer = '';

        try {
          while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            buffer += decoder.decode(value, { stream: true });
            const lines = buffer.split('\n');
            buffer = lines.pop() || '';

            for (const line of lines) {
              if (line.startsWith('data:')) {
                const dataStr = line.slice(5).trim();
                if (!dataStr) continue;

                try {
                  const data = JSON.parse(dataStr);
                  console.log('[CHAT] Parsed SSE data:', data);

                  if (data.type === 'content' && data.text) {
                    // Parse JSON response if needed
                    let actualText = data.text;
                    try {
                      if (data.text.trim().startsWith('{') && data.text.trim().endsWith('}')) {
                        const jsonResponse = JSON.parse(data.text);
                        actualText = jsonResponse.response || jsonResponse.message || jsonResponse.answer || data.text;
                        console.log('[CHAT] Extracted text from JSON:', actualText);
                      }
                    } catch (e) {
                      // Not JSON
                    }

                    accumulatedText += actualText;

                    // Send the message first time we get content
                    if (!hasStarted) {
                      hasStarted = true;
                      // Send initial message with the first chunk
                      const messageData = {
                        type: 'text',
                        text: actualText,
                        id: messageId,
                        role: 'assistant',
                      };
                      const sseMessage = `data: ${JSON.stringify(messageData)}\n\n`;
                      console.log('[CHAT] Sending initial SSE message:', sseMessage);
                      controller.enqueue(encoder.encode(sseMessage));
                    } else {
                      // Send subsequent chunks as deltas
                      const deltaData = {
                        type: 'text-delta',
                        textDelta: actualText,
                        id: messageId,
                      };
                      const sseDelta = `data: ${JSON.stringify(deltaData)}\n\n`;
                      console.log('[CHAT] Sending SSE delta:', sseDelta);
                      controller.enqueue(encoder.encode(sseDelta));
                    }
                  } else if (data.type === 'end') {
                    // Send finish signal
                    const finishData = {
                      type: 'finish',
                      id: messageId,
                    };
                    const sseFinish = `data: ${JSON.stringify(finishData)}\n\n`;
                    console.log('[CHAT] Sending SSE finish:', sseFinish);
                    controller.enqueue(encoder.encode(sseFinish));
                  }
                } catch (e) {
                  console.error('Failed to parse SSE data:', e);
                }
              }
            }
          }
        } finally {
          reader.releaseLock();
          controller.close();
        }
      }
    });

    return new Response(transformedStream, {
      headers: {
        "Content-Type": "text/event-stream",
        "Cache-Control": "no-cache",
        "Connection": "keep-alive",
      },
    });
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
