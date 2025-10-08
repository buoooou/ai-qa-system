"use client";
import { useState } from "react";

interface Props {
  sessions: { id: string; name: string }[];
  onSelectSession: (id: string) => void;
  onNewSession: () => void;
  onRenameSession: (id: string, name: string) => void;
  activeSessionId: string;
}

export default function ChatSidebar({
  sessions,
  onSelectSession,
  onNewSession,
  onRenameSession,
  activeSessionId,
}: Props) {
  const [editingId, setEditingId] = useState<string | null>(null);
  const [tempName, setTempName] = useState("");

  const handleDoubleClick = (id: string, name: string) => {
    setEditingId(id);
    setTempName(name);
  };

  const handleBlur = () => {
    if (editingId) {
      onRenameSession(editingId, tempName || "未命名会话");
      setEditingId(null);
      setTempName("");
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") handleBlur();
  };

  return (
    <div className="w-64 bg-gray-100 p-4 flex flex-col">
      <h2 className="text-lg font-bold mb-4">会话列表</h2>
      <button
        className="px-4 py-2 bg-blue-500 text-white rounded mb-2 hover:bg-blue-600"
        onClick={onNewSession}
      >
        新建会话
      </button>

      <div className="flex-1 mt-4 overflow-y-auto">
        {sessions.length === 0 ? (
          <div className="text-gray-500">暂无历史会话</div>
        ) : (
          sessions.map((session) => (
            <div
              key={session.id}
              onClick={() => onSelectSession(session.id)}
              className={`p-2 rounded mb-1 cursor-pointer ${
                session.id === activeSessionId
                  ? "bg-blue-200"
                  : "hover:bg-gray-200"
              }`}
              onDoubleClick={() => handleDoubleClick(session.id, session.name)}
            >
              {editingId === session.id ? (
                <input
                  autoFocus
                  className="w-full border-b border-blue-400 focus:outline-none bg-transparent"
                  value={tempName}
                  onChange={(e) => setTempName(e.target.value)}
                  onBlur={handleBlur}
                  onKeyDown={handleKeyDown}
                />
              ) : (
                session.name
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
}
