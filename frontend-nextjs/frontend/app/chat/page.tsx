"use client";

import { useState, useEffect } from "react";
import ChatSidebar from "./components/ChatSidebar";
import ChatWindow, { Message } from "./components/ChatWindow";
import { ProtectedRoute } from "@/components/auth/protected-route";
import { v4 as uuidv4 } from "uuid";

function ChatPageContent() {
  const [sessionId, setSessionId] = useState<string>(() => {
    return localStorage.getItem("lastSessionId") || uuidv4();
  });
  const [messages, setMessages] = useState<Message[]>([]);
  const [sessions, setSessions] = useState<{ id: string; name: string }[]>([]);

  // 初始化
  useEffect(() => {
    const savedSessions = JSON.parse(localStorage.getItem("sessions") || "[]");
    setSessions(savedSessions);

    const savedMessages = JSON.parse(
      localStorage.getItem(`messages-${sessionId}`) || "[]"
    );
    setMessages(savedMessages);
  }, []);

  // 切换会话
  const handleSelectSession = (id: string) => {
    const savedMessages = JSON.parse(
      localStorage.getItem(`messages-${id}`) || "[]"
    );
    setSessionId(id);
    setMessages(savedMessages);
    localStorage.setItem("lastSessionId", id);
  };

  // 新建会话
  const handleNewSession = () => {
    const newId = uuidv4();
    setSessionId(newId);
    setMessages([]);
    localStorage.setItem(`messages-${newId}`, JSON.stringify([]));
    localStorage.setItem("lastSessionId", newId);

    // 新建会话（不立即更新 Sidebar）
    // const newSession = { id: newId, name: "" };
    // const newSessions = [newSession, ...sessions];
    // setSessions(newSessions);
    // localStorage.setItem("sessions", JSON.stringify(newSessions));
  };

  // 创建会话（仅在首次发消息时调用）
  const createSessionIfNeeded = (id: string, title: string) => {
    setSessions((prev) => {
      if (prev.some((s) => s.id === id)) return prev; // 避免重复
      const newSession = { id, name: title };
      const newSessions = [newSession, ...prev];
      localStorage.setItem("sessions", JSON.stringify(newSessions));
      return newSessions;
    });
  };

  // 更新会话名（支持重命名）
  const handleRenameSession = (id: string, name: string) => {
    const updatedSessions = sessions.map((s) =>
      s.id === id ? { ...s, name } : s
    );
    setSessions(updatedSessions);
    localStorage.setItem("sessions", JSON.stringify(updatedSessions));
  };

  // 第一次发送消息时，如果是新会话，更新会话名为第一个问题
  useEffect(() => {
    if (messages.length === 1) {
      handleRenameSession(sessionId, messages[0].content.slice(0, 20)); // 取前 20 个字符
    }
  }, [messages]);

  return (
    <div className="flex h-screen bg-gray-50">
      <ChatSidebar
        sessions={sessions}
        onSelectSession={handleSelectSession}
        onNewSession={handleNewSession}
        onRenameSession={handleRenameSession}
        activeSessionId={sessionId}
      />
      <div className="flex-1 flex justify-center p-6 overflow-hidden">
        <ChatWindow
          messages={messages}
          setMessages={setMessages}
          sessionId={sessionId}
          onFirstMessage={(text) =>
            createSessionIfNeeded(sessionId, text.slice(0, 20))
          }
          onUpdateSessionName={(name) => handleRenameSession(sessionId, name)}
        />
      </div>
    </div>
  );
}

// 最终导出页面，带登录保护
export default function ChatPage() {
  return (
    <ProtectedRoute>
      <ChatPageContent />
    </ProtectedRoute>
  );
}
