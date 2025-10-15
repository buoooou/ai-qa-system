import { AxiosError } from "axios";
import NextAuth, { type DefaultSession } from "next-auth";
import type { DefaultJWT } from "next-auth/jwt";
import Credentials from "next-auth/providers/credentials";

import { loginViaGateway, registerViaGateway } from "@/lib/api/gateway";
import type {
  GatewayAuthResponse,
  GatewayErrorResponse,
} from "@/lib/api/types";
import { authConfig } from "./auth.config";

export type UserType = "guest" | "regular";

// 类型定义保持不变
declare module "next-auth" {
  interface Session extends DefaultSession {
    user: {
      id: string;
      type: UserType;
      accessToken?: string;
      username?: string;
      email?: string | null;
      nickname?: string | null;
      role?: string;
    } & DefaultSession["user"];
  }

  interface User {
    id?: string;
    email?: string | null;
    username?: string;
    nickname?: string | null;
    type: UserType;
    accessToken?: string;
    role?: string;
  }
}

declare module "next-auth/jwt" {
  interface JWT extends DefaultJWT {
    id: string;
    type: UserType;
    accessToken?: string;
    role?: string;
    username?: string;
    nickname?: string | null;
  }
}

export const {
  handlers: { GET, POST },
  auth,
  signIn,
  signOut,
} = NextAuth({
  ...authConfig,
  debug: process.env.NODE_ENV === "development", // 开启调试模式
  providers: [
    Credentials({
      id: "credentials",
      credentials: {},
      async authorize({ email, password }: any) {
        console.log("[Auth] Credentials authorize attempt:", { email, hasPassword: !!password });

        if (!email || !password) {
          console.log("[Auth] Missing email or password");
          return null;
        }

        try {
          console.log("[Auth] Calling loginViaGateway...");
          const response = await loginViaGateway({
            usernameOrEmail: email,
            password,
          });

          console.log("[Auth] Login successful, mapping response...");
          const user = mapGatewayAuthResponse(response);
          console.log("[Auth] Mapped user:", { id: user.id, username: user.username, type: user.type });

          return user;
        } catch (error) {
          console.error("[Auth] Login error:", error);
          const gatewayError = extractGatewayError(error);
          if (
            gatewayError?.code === 400 ||
            gatewayError?.message === "用户名或密码错误"
          ) {
            return null;
          }
          throw error;
        }
      },
    }),
    Credentials({
      authorize() {
        return {
          id: crypto.randomUUID(),
          email: null,
          username: "guest",
          nickname: "Guest",
          type: "guest" as const,
        };
      },
    }),
    Credentials({
      id: "register",
      credentials: {},
      async authorize({ email, password, username, nickname }: any) {
        if (!email || !password) {
          return null;
        }

        try {
          const response = await registerViaGateway({
            username: username ?? email,
            email,
            password,
            nickname,
          });
          return mapGatewayAuthResponse(response);
        } catch (error) {
          const gatewayError = extractGatewayError(error);
          if (
            gatewayError?.code === 400 &&
            gatewayError.message?.includes("存在")
          ) {
            const err = new Error("USER_EXISTS");
            err.name = "USER_EXISTS";
            throw err;
          }
          throw error;
        }
      },
    }),
  ],

  // ✅✅✅ --- 新增/修改的核心部分 --- ✅✅✅
  callbacks: {
    /**
     * 在JWT创建或更新时调用。
     * `user` 参数仅在【首次登录】时可用，它就是 `authorize` 函数返回的对象。
     */
    async jwt({ token, user }) {
      // 如果 user 对象存在，说明是刚登录，我们将 user 的信息持久化到 token 中。
      if (user) {
        token.id = user.id!;
        token.accessToken = user.accessToken;
        token.role = user.role;
        token.username = user.username;
        token.nickname = user.nickname;
        token.type = user.type;
      }
      return token;
    },

    /**
     * 在访问 session 时调用，例如通过 `useSession()`。
     * 我们在这里控制哪些信息可以从安全的、服务器端的 token 暴露给客户端。
     */
    async session({ session, token }) {
      // 将 token 中的数据同步到客户端可见的 session.user 对象中
      if (token && session.user) {
        session.user.id = token.id;
        session.user.role = token.role;
        session.user.username = token.username;
        session.user.nickname = token.nickname;
        session.user.type = token.type;
        
        // 暴露 accessToken 给客户端，用于调用后端 API
        session.user.accessToken = token.accessToken;
      }
      return session;
    },
  },
});

// 辅助函数保持不变
function extractGatewayError(error: unknown): GatewayErrorResponse | null {
  if (error instanceof AxiosError) {
    return error.response?.data as GatewayErrorResponse;
  }
  return null;
}

function mapGatewayAuthResponse(response: GatewayAuthResponse) {
  if (!response || !response.profile) {
    console.error("[Auth] Invalid response in mapGatewayAuthResponse:", response);
    throw new Error("Invalid authentication response");
  }

  return {
    id: String(response.profile.id),
    email: response.profile.email,
    username: response.profile.username,
    nickname: response.profile.nickname,
    type: "regular" as const,
    accessToken: response.token,
    role: response.profile.role,
  };
}