"use client";

import { useState, useRef, useEffect } from "react";
import ChatInput from "./ChatInput";
import { useAuth } from "@/contexts/auth-context";
import MessageBubble from "./MessageBubble";

export interface Message {
  id: string;
  content: string;
  role: "user" | "assistant";
  timestamp: Date;
}

interface Props {
  messages: Message[];
  setMessages: React.Dispatch<React.SetStateAction<Message[]>>;
  sessionId: string;
  onFirstMessage?: (text: string) => void;
  onUpdateSessionName?: (newName: string) => void;
}

export default function ChatWindow({
  messages,
  setMessages,
  sessionId,
  onFirstMessage,
  onUpdateSessionName,
}: Props) {
  const { token, userId } = useAuth();
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const [loading, setLoading] = useState(false);

  // 自动滚动
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  // 保存到 localStorage
  useEffect(() => {
    localStorage.setItem(`messages-${sessionId}`, JSON.stringify(messages));
  }, [messages, sessionId]);

  const TypingIndicator = () => (
    <div className="flex space-x-2 px-3 py-2 bg-gray-100 rounded-xl w-fit animate-pulse">
      <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce [animation-delay:-0.3s]" />
      <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce [animation-delay:-0.15s]" />
      <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" />
    </div>
  );

  const sendMessage = async (content: string) => {
    if (!token || !userId) return;

    const userMessage: Message = {
      id: crypto.randomUUID(),
      role: "user",
      content,
      timestamp: new Date(),
    };

    // 第一次发消息 → 自动创建会话
    setMessages((prev) => {
      // 第一次发消息 → 自动创建会话
      if (prev.length === 0 && onFirstMessage) {
        onFirstMessage(content);
      }
      return [...prev, userMessage];
    });

    // —— 新增：首次发送消息时更新会话名 ——
    if (messages.length === 0 && onUpdateSessionName) {
      onUpdateSessionName(content.slice(0, 20));
    }

    setLoading(true);

    try {
      // 先插入空的助手消息
      const assistantMessage: Message = {
        id: crypto.randomUUID(),
        role: "assistant",
        content: "",
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, assistantMessage]);

      // 调用真实后端 API
      const resp = await fetch("/api/chat", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
          "x-user-id": userId,
        },
        body: JSON.stringify({
          messages: [
            { id: userMessage.id, parts: [{ type: "text", text: content }] },
          ],
          sessionId,
        }),
      });

      if (!resp.ok) throw new Error(await resp.text());
      if (!resp.body) throw new Error("无响应体");

      const reader = resp.body.getReader();
      const decoder = new TextDecoder();
      let done = false;

      while (!done) {
        const { value, done: readerDone } = await reader.read();
        done = readerDone;
        if (value) {
          const chunk = decoder.decode(value, { stream: true });
          for (const line of chunk.split("\n").filter(Boolean)) {
            try {
              const json = JSON.parse(line);
              if (json.type === "message" && json.message) {
                const newText = json.message.parts?.[0]?.text ?? "";

                // 流式更新助手消息
                setMessages((prev) =>
                  prev.map((msg) =>
                    msg.id === assistantMessage.id
                      ? { ...msg, content: msg.content + newText }
                      : msg
                  )
                );
              }
            } catch (err) {
              console.error("解析流数据失败:", err);
            }
          }
        }
      }
    } catch (err) {
      console.error(err);
      const errorMsg: Message = {
        id: crypto.randomUUID(),
        role: "assistant",
        content: "出现错误，请稍后重试。",
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMsg]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="w-full max-w-7xl flex flex-col rounded-2xl shadow-lg bg-white border border-gray-200 overflow-hidden">
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {messages
          // 忽略空内容的助手消息
          .filter(
            (msg) => !(msg.role === "assistant" && msg.content.trim() === "")
          )
          .map((msg) => (
            <MessageBubble key={msg.id} message={msg} />
          ))}

        {/* 助手消息加载动画 */}
        {loading && <TypingIndicator />}

        <div ref={messagesEndRef} />
      </div>
      <ChatInput onSend={sendMessage} loading={loading} />
    </div>
  );
}
