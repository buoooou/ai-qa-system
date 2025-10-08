export interface User {
  id: string;
  username: string;
  nickname: string;
  avatar?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  nickname: string;
  password: string;
}

export interface AuthResponse {
  data: {
    token: string;
    user: User;
  };
}

export interface AuthContextType {
  user: User | null;
  token: string | null;
  userId: string | null;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (userData: RegisterRequest) => Promise<void>;
  logout: () => void;
  isLoading: boolean;
}
