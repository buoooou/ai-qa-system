import { auth } from "@/app/(auth)/auth";

export async function GET() {
  const session = await auth();

  return Response.json({
    session: session ? {
      user: {
        id: session.user?.id,
        email: session.user?.email,
        username: session.user?.username,
        hasAccessToken: !!session.user?.accessToken,
        accessTokenFirst10: session.user?.accessToken?.substring(0, 10),
      }
    } : null,
    timestamp: new Date().toISOString()
  });
}