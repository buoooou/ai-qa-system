import type { LoginRequest, RegisterRequest, AuthResponse, User } from "@/types/auth"

const API_BASE_URL = "http://localhost:8080"

class AuthAPI {
  private getAuthHeaders(token?: string) {
    const headers: Record<string, string> = {
      "Content-Type": "application/json",
    }

    if (token) {
      headers.Authorization = `Bearer ${token}`
    }

    return headers
  }

  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
      method: "POST",
      headers: this.getAuthHeaders(),
      body: JSON.stringify(credentials),
    })

    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || "Login failed")
    }

    return response.json()
  }

  async register(userData: RegisterRequest): Promise<AuthResponse> {
    const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
      method: "POST",
      headers: this.getAuthHeaders(),
      body: JSON.stringify(userData),
    })

    if (!response.ok) {
      const error = await response.text()
      throw new Error(error || "Registration failed")
    }

    return response.json()
  }

  async getCurrentUser(token: string): Promise<User> {
    const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
      method: "GET",
      headers: this.getAuthHeaders(token),
    })

    if (!response.ok) {
      throw new Error("Failed to get user info")
    }

    return response.json()
  }
}

export const authAPI = new AuthAPI()
