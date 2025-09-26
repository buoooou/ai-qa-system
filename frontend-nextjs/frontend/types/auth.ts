export interface UserProfile {
  id: number
  username: string
  email: string
  nickname?: string | null
  role: string
}

export interface LoginRequest {
  usernameOrEmail: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  nickname?: string
}

export interface AuthResponse {
  token: string
  expiresIn: number
  profile: UserProfile
}

export interface AuthContextType {
  user: UserProfile | null
  token: string | null
  login: (credentials: LoginRequest) => Promise<void>
  register: (userData: RegisterRequest) => Promise<void>
  logout: () => void
  isLoading: boolean
}
