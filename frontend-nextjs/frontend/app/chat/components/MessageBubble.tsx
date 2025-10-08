"use client";

import { Message } from "./ChatWindow";

interface Props {
  message: Message;
}

export default function MessageBubble({ message }: Props) {
  const isUser = message.role === "user";
  return (
    <div className={`flex ${isUser ? "justify-end" : "justify-start"} w-full`}>
      <div
        className={`px-4 py-2 rounded-2xl shadow-sm whitespace-pre-wrap break-words 
          ${
            isUser
              ? "bg-blue-500 text-white max-w-[70%]"
              : "bg-gray-100 text-gray-900 max-w-[90%]"
          }`}
      >
        {message.content}
      </div>
    </div>
  );
}
