export type GatewayAuthResponse = {
  token: string;
  expiresIn: number;
  profile: {
    id: number;
    username: string;
    email: string | null;
    nickname: string | null;
    role: string;
  };
};

export type GatewayErrorResponse = {
  code?: number;
  message?: string;
  success?: boolean;
};

export type GatewayChatSession = {
  id: number;
  userId: number;
  title: string;
  status: string;
  createdAt: string;
  updatedAt: string;
};

export type GatewayChatMessage = {
  id: number;
  sessionId: number;
  userId: number;
  question: string;
  answer: string;
  createdAt: string;
  updatedAt: string;
};

export type GatewayUserProfile = {
  id: number;
  username: string;
  email: string | null;
  nickname: string | null;
  role: string;
};

export type GatewayChatHistoryEntry = GatewayChatMessage;
