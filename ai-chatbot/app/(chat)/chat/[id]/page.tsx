import { cookies } from "next/headers";
import { notFound, redirect } from "next/navigation";

import { auth } from "@/app/(auth)/auth";
import { Chat } from "@/components/chat";
// import { DataStreamHandler } from "@/components/data-stream-handler"; // Disabled artifact system
import { DEFAULT_CHAT_MODEL } from "@/lib/ai/models";
import { gatewayGet } from "@/lib/api/gateway";
import type {
  GatewayChatMessage,
  GatewayChatSession,
} from "@/lib/api/types";
import { convertToUIMessages } from "@/lib/utils";

export default async function Page(props: { params: Promise<{ id: string }> }) {
  const params = await props.params;
  const { id } = params;
  const session = await auth();

  if (!session) {
    redirect("/api/auth/guest");
  }

  const chat = await gatewayGet<GatewayChatSession>(
    `/api/gateway/user/${session.user.id}/sessions/${id}`,
    undefined,
    { accessToken: session.user.accessToken }
  ).catch(() => null);

  if (!chat) {
    notFound();
  }

  if (!session.user || session.user.id !== String(chat.id)) {
    return notFound();
  }

  const messagesFromDb = await gatewayGet<GatewayChatMessage[]>(
    `/api/gateway/user/${session.user.id}/sessions/${id}/history`,
    undefined,
    { accessToken: session.user.accessToken }
  );

  const uiMessages = convertToUIMessages(messagesFromDb);

  const cookieStore = await cookies();
  const chatModelFromCookie = cookieStore.get("chat-model");

  if (!chatModelFromCookie) {
    return (
      <>
        <Chat
          autoResume={true}
          id={chat.id.toString()}
          initialChatModel={DEFAULT_CHAT_MODEL}
          initialLastContext={undefined}
          initialMessages={uiMessages}
          initialVisibilityType={chat.status === "private" ? "private" : "public"}
          isReadonly={session?.user?.id !== String(chat.id)}
        />
        {/* <DataStreamHandler /> */}
      </>
    );
  }

  return (
    <>
      <Chat
        autoResume={true}
        id={chat.id.toString()}
        initialChatModel={chatModelFromCookie.value}
        initialLastContext={undefined}
        initialMessages={uiMessages}
        initialVisibilityType={chat.status === "private" ? "private" : "public"}
        isReadonly={false}
        initialVisibilityType={chat.status === "private" ? "private" : "public"}
        isReadonly={false}
      />
      <DataStreamHandler />
    </>
  );
}
