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

    // Debug: Log response structure
    console.log("[CHAT] Gateway response type:", typeof response.data);
    console.log("[CHAT] Response data constructor:", response.data?.constructor?.name);
    console.log("[CHAT] Has getReader method:", typeof response.data?.getReader === 'function');

    // Transform Gateway SSE to AI SDK format
    const uiMessageStream = createUIMessageStream<ChatMessage>({
      execute: async ({ writer }) => {
        // Handle different response types
        let stream: ReadableStream<Uint8Array>;

        if (response.data instanceof ReadableStream) {
          stream = response.data;
        } else if (response.data && typeof response.data === 'object' && 'pipe' in response.data) {
          // Handle Node.js stream
          stream = Readable.fromWeb(response.data as any);
        } else if (typeof response.data === 'string') {
          // Handle error response as string
          console.error('[CHAT] Received string instead of stream:', response.data);
          writer.write({
            type: 'error',
            errorText: `Expected stream but received string: ${response.data.substring(0, 100)}`,
          });
          return;
        } else {
          console.error('[CHAT] Unexpected response type:', typeof response.data, response.data);
          writer.write({
            type: 'error',
            errorText: `Expected ReadableStream but received ${typeof response.data}`,
          });
          return;
        }

        const reader = stream.getReader();
        const decoder = new TextDecoder();
        let buffer = '';
        let messageId = requestBody.message.id;
        let currentMessage: ChatMessage = {
          id: messageId,
          role: 'assistant',
          parts: [{ type: 'text', text: '' }]
        };

        try {
          console.log('[CHAT] Starting to read stream...');
          while (true) {
            const { done, value } = await reader.read();
            if (done) {
              console.log('[CHAT] Stream reading completed');
              if (buffer) {
                console.log('[CHAT] Final buffer content:', JSON.stringify(buffer));
              } else {
                console.log('[CHAT] Stream ended with no buffer content');
              }
              break;
            }

            const chunkText = decoder.decode(value, { stream: true });
            console.log('[CHAT] Received chunk:', JSON.stringify(chunkText));
            buffer += chunkText;
            const lines = buffer.split('\n');
            buffer = lines.pop() || '';

            for (const line of lines) {
              // Log all lines for debugging
              if (line.trim()) {
                console.log('[CHAT] Processing line:', JSON.stringify(line));
              }

              // Check for data: prefix (with or without space)
              if (line.startsWith('data:')) {
                const dataStr = line.slice(5).trim(); // Remove "data:" (5 chars)
                console.log('[CHAT] SSE data:', dataStr);
                if (!dataStr) continue;

                // Handle [DONE] signal from Gemini
                if (dataStr === '[DONE]') {
                  console.log('[CHAT] Received [DONE] signal');
                  writer.write({
                    type: 'finish',
                  });
                  break;
                }

                try {
                  const data = JSON.parse(dataStr);
                  console.log('[CHAT] Parsed SSE data:', data);

                  if (data.type === 'content' && data.text) {
                    let actualText = data.text;

                    // Try to extract JSON content if it looks like JSON
                    try {
                      // Check if text is or contains JSON with response/message/answer field
                      const jsonMatch = data.text.match(/\{[^{}]*"(?:response|message|answer)"[^{}]*\}/);
                      if (jsonMatch) {
                        const jsonResponse = JSON.parse(jsonMatch[0]);
                        actualText = jsonResponse.response || jsonResponse.message || jsonResponse.answer || actualText;
                        console.log('[CHAT] Extracted JSON response:', actualText);
                      } else if (data.text.trim().startsWith('{') && data.text.trim().endsWith('}')) {
                        // Single complete JSON object
                        const jsonResponse = JSON.parse(data.text);
                        actualText = jsonResponse.response || jsonResponse.message || jsonResponse.answer || actualText;
                        console.log('[CHAT] Parsed single JSON response:', actualText);
                      }
                    } catch (jsonError) {
                      // Not JSON, use as plain text
                      console.log('[CHAT] Using as plain text:', actualText);
                    }

                    // Append content to message
                    if (currentMessage.parts[0].type === 'text') {
                      currentMessage.parts[0].text += actualText;
                    }

                    // Send incremental update to AI SDK (correct format)
                    writer.write({
                      type: 'text-delta',
                      textDelta: actualText,
                    });
                  } else if (data.type === 'end') {
                    // Mark message as complete
                    writer.write({
                      type: 'finish',
                    });
                  }
                } catch (e) {
                  console.error('[CHAT] Failed to parse SSE data:', e, dataStr);
                  // Continue processing other lines even if one fails
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
