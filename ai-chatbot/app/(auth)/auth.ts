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

  // biome-ignore lint/nursery/useConsistentTypeDefinitions: "Required"
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
  providers: [
    Credentials({
      credentials: {},
      async authorize({ email, password }: any) {
        if (!email || !password) {
          return null;
        }

        try {
          const response = await loginViaGateway({
            usernameOrEmail: email,
            password,
          });

          return mapGatewayAuthResponse(response);
        } catch (error) {
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
});

function extractGatewayError(error: unknown): GatewayErrorResponse | null {
  if (error instanceof AxiosError) {
    return error.response?.data as GatewayErrorResponse;
  }
  return null;
}

function mapGatewayAuthResponse(response: GatewayAuthResponse) {
  return {
    id: String(response.profile.id),
    email: response.profile.email,
    username: response.profile.username,
    nickname: response.profile.nickname,
    type: "regular" as const,
    accessToken: response.token,
    role: response.profile.role,
  } satisfies {
    id: string;
    email: string;
    username: string;
    nickname: string | null;
    type: UserType;
    accessToken: string;
    role: string;
  };
}
