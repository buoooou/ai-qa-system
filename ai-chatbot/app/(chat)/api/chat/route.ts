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

      // Create AI SDK v5 compatible SSE stream
    const encoder = new TextEncoder();
    const decoder = new TextDecoder();
    let messageId = requestBody.message.id;
    let hasStarted = false;

    const sseStream = new ReadableStream({
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

                  // Handle session ID from backend
                  if (data.type === 'session-id' && data.sessionId) {
                    const sessionData = `data: ${JSON.stringify({
                      type: 'session-id',
                      sessionId: data.sessionId
                    })}\n\n`;
                    console.log('[CHAT] Forwarding session-id:', sessionData);
                    controller.enqueue(encoder.encode(sessionData));
                    continue;
                  }

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

                    // Send text-start only on first chunk
                    if (!hasStarted) {
                      hasStarted = true;
                      const startData = `data: ${JSON.stringify({
                        type: 'text-start',
                        id: messageId
                      })}\n\n`;
                      console.log('[CHAT] Sending text-start:', startData);
                      controller.enqueue(encoder.encode(startData));
                    }

                    // Send text-delta for the actual content
                    const deltaData = `data: ${JSON.stringify({
                      type: 'text-delta',
                      id: messageId,
                      delta: actualText
                    })}\n\n`;
                    console.log('[CHAT] Sending text-delta:', deltaData);
                    controller.enqueue(encoder.encode(deltaData));
                  } else if (data.type === 'end') {
                    // Send text-end
                    const endData = `data: ${JSON.stringify({
                      type: 'text-end',
                      id: messageId
                    })}\n\n`;
                    console.log('[CHAT] Sending text-end:', endData);
                    controller.enqueue(encoder.encode(endData));
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

    return new Response(sseStream, {
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
