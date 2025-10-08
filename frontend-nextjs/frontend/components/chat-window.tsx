"use client";

import { useChat } from "@ai-sdk/react";
import { DefaultChatTransport } from "ai";
import { MessageBubble } from "./message-bubble";
import { ChatInput } from "./chat-input";
import { ChatHeader } from "./chat-header";
import { useEffect, useRef, useCallback } from "react";
import { Bot, Sparkles } from "lucide-react";
import { useAuth } from "@/contexts/auth-context";

interface ChatWindowProps {
  conversationId?: string;
  conversationTitle?: string;
  initialMessages?: Array<{
    id: string;
    role: "user" | "assistant";
    content: string;
    timestamp: Date;
  }>;
  onMessageAdded?: (message: {
    role: "user" | "assistant";
    content: string;
  }) => void;
  onFirstMessage?: (content: string) => void;
}

export function ChatWindow({
  conversationId,
  conversationTitle,
  initialMessages = [],
  onMessageAdded,
  onFirstMessage,
}: ChatWindowProps) {
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const { token, userId } = useAuth();

  const { messages, sendMessage, status, setMessages } = useChat({
    transport: new DefaultChatTransport({
      api: "/api/chat",
      headers: {
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        "x-user-id": userId ?? "1", // ✅ 自定义 header
      },
    }),
    onFinish: (result) => {
      if (onMessageAdded) {
        const msg = result.message;
        console.log("onFinish Result Message:", msg);
        let content = "";
        if (msg.parts?.[0]?.type === "text") {
          content = (msg.parts[0] as { type: "text"; text: string }).text;
        }
        onMessageAdded({ role: "assistant", content });
      }
    },
  });

  useEffect(() => {
    if (initialMessages.length > 0) {
      setMessages(
        initialMessages.map((msg) => ({
          id: msg.id,
          role: msg.role,
          parts: [{ type: "text", text: msg.content }],
          timestamp: msg.timestamp,
        }))
      );
    }
  }, [initialMessages, setMessages]);

  // Auto-scroll to bottom when new messages arrive
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSendMessage = useCallback(
    (content: string) => {
      if (messages.length === 0 && onFirstMessage) {
        onFirstMessage(content);
      }

      if (onMessageAdded) {
        onMessageAdded({ role: "user", content });
      }

      sendMessage({ parts: [{ type: "text", text: content }] });
    },
    [messages.length, onFirstMessage, onMessageAdded, sendMessage]
  );

  if (!conversationId && messages.length === 0) {
    return (
      <div className="flex-1 flex flex-col">
        <ChatHeader />

        {/* Welcome Screen */}
        <div className="flex-1 flex items-center justify-center">
          <div className="text-center max-w-md mx-auto px-4">
            <div className="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
              <Bot className="w-8 h-8 text-primary" />
            </div>
            <h1 className="text-2xl font-semibold mb-2 text-balance">
              AI 聊天助手
            </h1>
            <p className="text-muted-foreground mb-6 text-pretty">
              我是您的智能助手，可以帮助您解答问题、提供建议或进行有趣的对话。支持
              Markdown 格式和代码高亮。
            </p>

            {/* Example prompts */}
            <div className="space-y-2">
              <p className="text-sm font-medium text-foreground mb-3">
                试试这些问题：
              </p>
              <div className="grid gap-2">
                {[
                  "解释一下人工智能的基本概念",
                  "帮我写一个简单的 Python 函数",
                  "推荐一些学习编程的资源",
                  "用 Markdown 格式介绍一下 React",
                ].map((prompt, index) => (
                  <button
                    key={index}
                    onClick={() => handleSendMessage(prompt)}
                    className="text-left p-3 rounded-lg border border-border hover:bg-accent hover:text-accent-foreground transition-colors text-sm"
                  >
                    <Sparkles className="w-4 h-4 inline mr-2 text-primary" />
                    {prompt}
                  </button>
                ))}
              </div>
            </div>
          </div>
        </div>

        <ChatInput
          onSendMessage={handleSendMessage}
          disabled={status === "submitted" || status === "streaming"}
          placeholder="开始新的对话..."
        />
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col">
      <ChatHeader title={conversationTitle} />

      {/* Messages Area */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {messages
          .filter(
            (
              message
            ): message is typeof message & { role: "user" | "assistant" } =>
              message.role === "user" || message.role === "assistant"
          )
          .map((message) => (
            <MessageBubble
              key={message.id}
              role={message.role}
              content={
                message.parts?.[0]?.type === "text"
                  ? (message.parts[0] as { type: "text"; text: string }).text
                  : ""
              }
              timestamp={new Date()}
            />
          ))}

        {/* Loading indicator */}
        {(status === "submitted" || status === "streaming") && (
          <div className="flex gap-3 mb-4">
            <div className="flex-shrink-0 w-8 h-8 bg-card rounded-full flex items-center justify-center">
              <Bot className="w-4 h-4 text-card-foreground" />
            </div>
            <div className="bg-card text-card-foreground rounded-lg px-4 py-2 mr-12">
              <div className="flex items-center gap-1">
                <div className="w-2 h-2 bg-current rounded-full animate-bounce [animation-delay:-0.3s]"></div>
                <div className="w-2 h-2 bg-current rounded-full animate-bounce [animation-delay:-0.15s]"></div>
                <div className="w-2 h-2 bg-current rounded-full animate-bounce"></div>
              </div>
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      <ChatInput
        onSendMessage={handleSendMessage}
        disabled={status === "submitted" || status === "streaming"}
      />
    </div>
  );
}
