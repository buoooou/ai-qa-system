"use server";

import { cookies } from "next/headers";
import { auth } from "@/app/(auth)/auth";
import { gatewayPatch } from "@/lib/api/gateway";

export async function saveChatModelAsCookie(model: string) {
  const cookieStore = await cookies();
  cookieStore.set("chat-model", model);
}

export async function updateChatVisibility({
  chatId,
  visibility,
}: {
  chatId: string;
  visibility: "public" | "private";
}) {
  const session = await auth();

  if (!session?.user?.accessToken) {
    throw new Error("Not authenticated");
  }

  await gatewayPatch(
    `/api/gateway/user/${session.user.id}/sessions/${chatId}`,
    { status: visibility },
    undefined,
    { accessToken: session.user.accessToken }
  );
}
