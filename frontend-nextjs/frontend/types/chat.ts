import { UIMessage } from "ai"

export interface Conversation {
  id: string
  title: string
  messages: Array<{
    id: string
    role: "user" | "assistant"
    content: string
    timestamp: Date
  }>
  createdAt: Date
  updatedAt: Date
}

export interface ChatMessage {
  userId: string;
  answer: UIMessage[];
}

export interface SaveHistoryRequest {
  userId: string;
  answer: string;
}

export interface QaContextType {
  saveHistory: (history: ChatMessage) => Promise<void>;
  getHistory: (
    conversationId: string
  ) => Promise<Conversation["messages"] | undefined>;
}
