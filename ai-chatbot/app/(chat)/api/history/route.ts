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

  if (!session.user.accessToken) {
    console.error("No accessToken in session for history", {
      session,
      userKeys: session.user ? Object.keys(session.user) : 'no user',
      hasAccessToken: !!session.user?.accessToken
    });
    // Return empty history instead of error to avoid blocking the UI
    return Response.json({ chats: [], hasMore: false });
  }

  let url = `/api/gateway/user/${session.user.id}/sessions?limit=${limit}`;

  if (startingAfter) {
    url += `&starting_after=${startingAfter}`;
  }

  if (endingBefore) {
    url += `&ending_before=${endingBefore}`;
  }

  const response = await gatewayGet<GatewayChatSession[]>(
    url,
    undefined,
    { accessToken: session.user.accessToken }
  );

  // Transform the response to match the expected format
  const chats = {
    chats: response || [],
    hasMore: false // TODO: Implement proper pagination logic when backend supports it
  };

  return Response.json(chats);
}
