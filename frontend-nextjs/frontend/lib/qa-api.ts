import { ChatMessage, Conversation, GetHistoryResponse, SaveHistoryRequest } from "@/types/chat";
import { UIMessage } from "ai";

class QaAPI {
  private getAuthHeaders(token?: string) {
    const headers: Record<string, string> = {
      "Content-Type": "application/json",
    };

    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    return headers;
  }

  async getHistory(conversationId: String): Promise<GetHistoryResponse> {
    const tekon = localStorage.getItem("auth_token");
    const response = await fetch(
      `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/qa/gethistory/${conversationId}`,
      {
        method: "GET",
        headers: this.getAuthHeaders(tekon || ""),
      }
    ).then(async (response) => {
      if (!response.ok) {
        return null;
      }
      return response.json();
    });

    const data: GetHistoryResponse = {
      id: response.data.id?.toString() || "",
      userId: response.data.userId?.toString() || "",
      question: response.data.question?.toString() || "",
      answer: JSON.parse(response.data.answer),
      sessionId: response.data.sessionId || "",
      createTime: response.data.createTime || new Date(),
    }; 
    return data as GetHistoryResponse;
  }

  async saveHistory(historyMessages: ChatMessage): Promise<boolean> {
    const tekon = localStorage.getItem("auth_token");
    const saveBody: SaveHistoryRequest = {
      userId: historyMessages.userId,
      question: "",
      answer: JSON.stringify(historyMessages.answer),
      sessionId: historyMessages.sessionId,
    };
    const response = await fetch(
      `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/qa/save/`,
      {
        method: "POST",
        headers: this.getAuthHeaders(tekon || ""),
        body: JSON.stringify(saveBody),
      }
    ).then(async (response) => {
      if (!response.ok) {
        const error = await response.text();
        throw new Error(error || "'Failed to fetch conversation history'");
      }
      return response.json();
    });
    return response.data as boolean;
  }
  //   async register(userData: RegisterRequest): Promise<AuthResponse> {
  //     const response = await fetch(`${process.env.API_BASE_URL}/api/user/register`,
  //       {
  //         method: "POST",
  //         headers: this.getAuthHeaders(),
  //         body: JSON.stringify(userData),
  //       }
  //     ).then(async (response) => {
  //       if (!response.ok) {
  //         const error = await response.text();
  //         throw new Error(error || "Register failed");
  //       }
  //       return response.json();
  //     });
  //     return response.data as AuthResponse;
  //   }

  //   async getCurrentUser(token: string): Promise<User> {
  //     const response = await fetch(`${process.env.API_BASE_URL}/api/user/me`, {
  //       method: "GET",
  //       headers: this.getAuthHeaders(token),
  //     }).then(async (response) => {
  //       if (!response.ok) {
  //         const error = await response.text();
  //         throw new Error(error || "Failed to get user info");
  //       }
  //       return response.json();
  //     });
  //     return response.data as User;
  //   }
}

export const qaAPI = new QaAPI();
