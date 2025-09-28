import { auth } from "@/app/(auth)/auth";
import { gatewayPost } from "@/lib/api/gateway";
import { ChatSDKError } from "@/lib/errors";
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

    const stream = await gatewayPost<ReadableStream>(
      "/api/gateway/qa/chat",
      { ...requestBody, userId: Number.parseInt(session.user.id, 10) },
      {
        responseType: "stream",
      },
      {
        accessToken: session.user.accessToken,
      }
    );

    return new Response(stream, {
      headers: {
        "Content-Type": "text/event-stream",
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
