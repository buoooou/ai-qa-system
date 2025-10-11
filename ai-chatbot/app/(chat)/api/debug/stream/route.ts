import { auth } from "@/app/(auth)/auth";
import { streamGatewayChat } from "@/lib/api/gateway";

export async function POST(request: Request) {
  const session = await auth();

  if (!session?.user?.accessToken) {
    return new Response("Unauthorized", { status: 401 });
  }

  try {
    const response = await streamGatewayChat(
      {
        userId: Number.parseInt(session.user.id, 10),
        question: "Hello, this is a test message",
        history: [],
        sessionTitle: "Debug Test",
      },
      session.user.accessToken
    );

    // Debug: Return the stream directly without any transformation
    return response;
  } catch (error) {
    console.error('[DEBUG] Stream error:', error);
    return new Response(`Error: ${error.message}`, { status: 500 });
  }
}