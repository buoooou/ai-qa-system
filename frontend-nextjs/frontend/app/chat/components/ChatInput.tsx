"use client";

import { useState, KeyboardEvent } from "react";

interface Props {
  onSend: (content: string) => void;
  loading: boolean;
}

export default function ChatInput({ onSend, loading }: Props) {
  const [value, setValue] = useState("");

  const handleSend = () => {
    if (!value.trim() || loading) return;
    onSend(value);
    setValue("");
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div className="p-4 border-t border-gray-200 flex items-center gap-2">
      <textarea
        className="flex-1 border rounded p-2 resize-none h-12"
        placeholder="请输入消息..."
        value={value}
        onChange={(e) => setValue(e.target.value)}
        onKeyDown={handleKeyDown}
      />
      <button
        onClick={handleSend}
        disabled={loading}
        className="px-4 py-2 bg-blue-500 text-white rounded disabled:bg-gray-300"
      >
        发送
      </button>
    </div>
  );
}
