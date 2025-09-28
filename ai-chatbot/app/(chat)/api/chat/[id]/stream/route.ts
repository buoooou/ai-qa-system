import { createUIMessageStream, JsonToSseTransformStream } from "ai";
import { differenceInSeconds, parseISO } from "date-fns";
import { auth } from "@/app/(auth)/auth";
import { gatewayGet } from "@/lib/api/gateway";
import { ChatSDKError } from "@/lib/errors";
import type { GatewayChatMessage, GatewayChatSession } from "@/lib/api/types";
import type { ChatMessage } from "@/lib/types";
import { getStreamContext } from "../../route";

export async function GET(
  _: Request,
  { params }: { params: Promise<{ id: string }> }
) {
  const { id: chatId } = await params;

  const streamContext = getStreamContext();
  const resumeRequestedAt = new Date();

  if (!streamContext) {
    return new Response(null, { status: 204 });
  }

  if (!chatId) {
    return new ChatSDKError("bad_request:api").toResponse();
  }

  const session = await auth();

  if (!session?.user) {
    return new ChatSDKError("unauthorized:chat").toResponse();
  }

  const chat = await gatewayGet<GatewayChatSession>(
    `/api/gateway/user/${session.user.id}/sessions/${chatId}`,
    undefined,
    { accessToken: session.user.accessToken }
  ).catch(() => null);

  if (!chat) {
    return new ChatSDKError("not_found:chat").toResponse();
  }

  if (chat.status === "private" && session.user.id !== String(chat.userId ?? "")) {
    return new ChatSDKError("forbidden:chat").toResponse();
  }

  const streamId = getStreamContext()?.latestStreamId ?? null;

  if (!streamId) {
    return new ChatSDKError("not_found:stream").toResponse();
  }

  const emptyDataStream = createUIMessageStream<ChatMessage>({
    // biome-ignore lint/suspicious/noEmptyBlockStatements: "Needs to exist"
    execute: () => {},
  });

  const stream = await streamContext.resumableStream(streamId, () =>
    emptyDataStream.pipeThrough(new JsonToSseTransformStream())
  );

  /*
   * For when the generation is streaming during SSR
   * but the resumable stream has concluded at this point.
   */
  if (!stream) {
    const messages = await gatewayGet<GatewayChatMessage[]>(
      `/api/gateway/user/${session.user.id}/sessions/${chatId}/history`,
      { params: { limit: 1 } },
      { accessToken: session.user.accessToken }
    ).catch(() => []);
    const mostRecentMessage = messages.at(-1);

    if (!mostRecentMessage) {
      return new Response(emptyDataStream, { status: 200 });
    }

    if (mostRecentMessage.role !== "assistant") {
      return new Response(emptyDataStream, { status: 200 });
    }

    const messageCreatedAt = parseISO(mostRecentMessage.updatedAt ?? mostRecentMessage.createdAt);

    if (differenceInSeconds(resumeRequestedAt, messageCreatedAt) > 15) {
      return new Response(emptyDataStream, { status: 200 });
    }

    const restoredStream = createUIMessageStream<ChatMessage>({
      execute: ({ writer }) => {
        writer.write({
          type: "data-appendMessage",
          data: JSON.stringify(mostRecentMessage),
          transient: true,
        });
      },
    });

    return new Response(
      restoredStream.pipeThrough(new JsonToSseTransformStream()),
      { status: 200 }
    );
  }

  return new Response(stream, { status: 200 });
}
