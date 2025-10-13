"use client";

import type { UseChatHelpers } from "@ai-sdk/react";
import { useEffect } from "react";
import { useDataStream } from "@/components/data-stream-provider";
import type { ChatMessage } from "@/lib/types";

export type UseAutoResumeParams = {
  autoResume: boolean;
  initialMessages: ChatMessage[];
  resumeStream: UseChatHelpers<ChatMessage>["resumeStream"];
  setMessages: UseChatHelpers<ChatMessage>["setMessages"];
};

export function useAutoResume({
  autoResume,
  initialMessages,
  resumeStream,
  setMessages,
}: UseAutoResumeParams) {
  const { dataStream } = useDataStream();

  useEffect(() => {
    if (!autoResume) {
      return;
    }

    const mostRecentMessage = initialMessages.at(-1);

    // Only resume if the last message is a user message AND there's no assistant response after it
    // This prevents trying to resume completed conversations
    if (mostRecentMessage?.role === "user") {
      // Check if there are any assistant messages after this user message
      const hasAssistantResponse = initialMessages.some(
        (msg, index) => index > initialMessages.length - 1 && msg.role === "assistant"
      );

      // Only resume if no assistant response exists and the message was created recently (within last 30 seconds)
      if (!hasAssistantResponse && mostRecentMessage.createdAt) {
        const messageTime = new Date(mostRecentMessage.createdAt);
        const now = new Date();
        const timeDiffSeconds = (now.getTime() - messageTime.getTime()) / 1000;

        // Only resume if the message is very recent (likely still being processed)
        if (timeDiffSeconds < 30) {
          resumeStream();
        }
      }
    }

    // we intentionally run this once
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [autoResume, initialMessages.at, resumeStream]);

  useEffect(() => {
    if (!dataStream) {
      return;
    }
    if (dataStream.length === 0) {
      return;
    }

    const dataPart = dataStream[0];

    if (dataPart.type === "data-appendMessage") {
      const message = JSON.parse(dataPart.data);
      setMessages([...initialMessages, message]);
    }
  }, [dataStream, initialMessages, setMessages]);
}
