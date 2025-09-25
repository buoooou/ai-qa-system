export interface User {
  id: number;
  userName: string;
  nickname: string;
  createTime: string;
  updateTime: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  userInfo(userInfo: any): unknown;
  user: User;
  token: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  nickname: string;
}

export interface QuestionRequest {
  question: string;
  userId?: number;
  sessionId?: string;
  questionType?: string;
}

export interface QuestionResponse {
  id: number;
  question: string;
  answer: string;
  userId: number;
  sessionId: string;
  responseTime: number;
  createTime: string;
  questionType: string;
  modelVersion: string;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface ChatMessage {
  id: string;
  type: 'user' | 'bot';
  content: string;
  timestamp: Date;
}