import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { Chat } from "@/components/chat";
// import { DataStreamHandler } from "@/components/data-stream-handler"; // Disabled artifact system
import { DEFAULT_CHAT_MODEL } from "@/lib/ai/models";
import { generateUUID } from "@/lib/utils";
import { auth } from "../(auth)/auth";

export default async function Page() {
  // 添加调试日志
  console.log("[Page] Checking authentication...");

  const session = await auth();

  // 添加详细的 session 信息日志
  console.log("[Page] Session result:", {
    hasSession: !!session,
    userId: session?.user?.id,
    username: session?.user?.username,
    expires: session?.expires
  });

  if (!session) {
    console.log("[Page] No session found, redirecting to login...");
    redirect("/login");
  }

  console.log("[Page] Session valid, rendering chat page...");
  const id = generateUUID();

  const cookieStore = await cookies();
  const modelIdFromCookie = cookieStore.get("chat-model");

  if (!modelIdFromCookie) {
    return (
      <>
        <Chat
          autoResume={false}
          id={id}
          initialChatModel={DEFAULT_CHAT_MODEL}
          initialMessages={[]}
          initialVisibilityType="private"
          isReadonly={false}
          key={id}
        />
        {/* <DataStreamHandler /> */}
      </>
    );
  }

  return (
    <>
      <Chat
        autoResume={false}
        id={id}
        initialChatModel={modelIdFromCookie.value}
        initialMessages={[]}
        initialVisibilityType="private"
        isReadonly={false}
        key={id}
      />
      {/* <DataStreamHandler /> */}
    </>
  );
}