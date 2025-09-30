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
  sessionId: String;
}

export interface SaveHistoryRequest {
  userId: string;
  question: String;
  answer: string;
  sessionId: String;
}

export interface GetHistoryResponse {
  id: String;
  userId: String;
  question: String;
  answer: UIMessage[];
  sessionId: String;
  createTime: Date;
}

export interface QaContextType {
  saveHistory: (history: ChatMessage) => Promise<void>;
  getHistory: (conversationId: string) => Promise<GetHistoryResponse | null>;
}
