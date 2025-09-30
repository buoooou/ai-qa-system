import type { LoginRequest, RegisterRequest, AuthResponse, User } from "@/types/auth"

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
    const response = await fetch(`http://3.25.253.118:8080/api/user/login`, {
      method: "POST",
      headers: this.getAuthHeaders(),
      body: JSON.stringify(credentials),
    }).then(async (response) => {
      if (!response.ok) {
        const error = await response.text();
        throw new Error(error || "Login failed");
      }
      return response.json();
    });
    return response.data as AuthResponse;
  }

  async register(userData: RegisterRequest): Promise<AuthResponse> {
    const response = await fetch(`http://3.25.253.118:8080/api/user/register`, {
      method: "POST",
      headers: this.getAuthHeaders(),
      body: JSON.stringify(userData),
    }).then(async (response) => {
      if (!response.ok) {
        const error = await response.text();
        throw new Error(error || "Register failed");
      }
      return response.json();
    });
    return response.data as AuthResponse;
  }

  async getCurrentUser(token: string): Promise<User> {
    const response = await fetch(`http://3.25.253.118:8080/api/user/me`, {
      method: "GET",
      headers: this.getAuthHeaders(token),
    }).then(async (response) => {
      if (!response.ok) {
        const error = await response.text();
        throw new Error(error || "Failed to get user info");
      }
      return response.json();
    });
    return response.data as User;
  }
}

export const authAPI = new AuthAPI()
