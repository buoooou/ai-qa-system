import type { NextRequest } from "next/server";
import { auth } from "@/app/(auth)/auth";
import { gatewayGet } from "@/lib/api/gateway";
import type { GatewayChatSession } from "@/lib/api/types";
import { ChatSDKError } from "@/lib/errors";

export async function GET(request: NextRequest) {
  const { searchParams } = request.nextUrl;

  const limit = Number.parseInt(searchParams.get("limit") || "10", 10);
  const startingAfter = searchParams.get("starting_after");
  const endingBefore = searchParams.get("ending_before");

  if (startingAfter && endingBefore) {
    return new ChatSDKError(
      "bad_request:api",
      "Only one of starting_after or ending_before can be provided."
    ).toResponse();
  }

  const session = await auth();

  if (!session?.user) {
    return new ChatSDKError("unauthorized:chat").toResponse();
  }

  const chats = await gatewayGet<{
    chats: GatewayChatSession[];
    hasMore: boolean;
  }>(
    `/api/gateway/user/${session.user.id}/sessions`,
    {
      params: {
        limit,
        startingAfter: startingAfter ?? undefined,
        endingBefore: endingBefore ?? undefined,
      },
    },
    { accessToken: session.user.accessToken }
  );

  return Response.json(chats);
}
