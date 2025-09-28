import axios, { AxiosError, type AxiosRequestConfig } from "axios";
import type {
  GatewayAuthResponse,
  GatewayChatHistoryEntry,
  GatewayChatSession,
  GatewayErrorResponse,
  GatewayUserProfile,
} from "@/lib/api/types";

type GatewaySession = {
  accessToken?: string;
};

const baseURL =
  process.env.NEXT_PUBLIC_GATEWAY_URL ?? process.env.GATEWAY_URL ?? undefined;

if (!baseURL) {
  console.warn(
    "[gateway] NEXT_PUBLIC_GATEWAY_URL is not defined. Requests will use relative paths and likely fail in production."
  );
}

const gatewayClient = axios.create({
  baseURL,
  timeout: 15_000,
});

const withAuth = <_T>(
  config: AxiosRequestConfig = {},
  session?: GatewaySession
) => {
  const headers = { ...config.headers };

  if (session?.accessToken) {
    headers.Authorization = `Bearer ${session.accessToken}`;
  }

  return { ...config, headers } satisfies AxiosRequestConfig;
};

export const gatewayGet = async <T>(
  url: string,
  config?: AxiosRequestConfig,
  session?: GatewaySession
) => {
  const response = await gatewayClient.get<T>(url, withAuth(config, session));
  return response.data;
};

export const gatewayPost = async <T, B = unknown>(
  url: string,
  body?: B,
  config?: AxiosRequestConfig,
  session?: GatewaySession
) => {
  const response = await gatewayClient.post<T>(
    url,
    body,
    withAuth(config, session)
  );
  return response.data;
};

export const gatewayPatch = async <T, B = unknown>(
  url: string,
  body?: B,
  config?: AxiosRequestConfig,
  session?: GatewaySession
) => {
  const response = await gatewayClient.patch<T>(
    url,
    body,
    withAuth(config, session)
  );
  return response.data;
};

export const gatewayDelete = async <T>(
  url: string,
  config?: AxiosRequestConfig,
  session?: GatewaySession
) => {
  const response = await gatewayClient.delete<T>(
    url,
    withAuth(config, session)
  );
  return response.data;
};

export const loginViaGateway = async (
  payload: { usernameOrEmail: string; password: string },
  session?: GatewaySession
) =>
  gatewayPost<GatewayAuthResponse, typeof payload>(
    "/api/gateway/auth/login",
    payload,
    undefined,
    session
  );

export const registerViaGateway = async (
  payload: {
    username: string;
    email: string;
    password: string;
    nickname?: string;
  },
  session?: GatewaySession
) =>
  gatewayPost<GatewayAuthResponse, typeof payload>(
    "/api/gateway/auth/register",
    payload,
    undefined,
    session
  );

export const fetchUserSessions = async (userId: string, accessToken?: string) =>
  gatewayGet<GatewayChatSession[]>(
    `/api/gateway/user/${userId}/sessions`,
    undefined,
    accessToken ? { accessToken } : undefined
  );

export const createUserSession = async (
  userId: string,
  payload: { title?: string },
  accessToken?: string
) =>
  gatewayPost<GatewayChatSession, typeof payload>(
    `/api/gateway/user/${userId}/sessions`,
    payload,
    undefined,
    accessToken ? { accessToken } : undefined
  );

export const getUserSession = async (
  userId: string,
  sessionId: number,
  accessToken?: string
) =>
  gatewayGet<GatewayChatSession>(
    `/api/gateway/user/${userId}/sessions/${sessionId}`,
    undefined,
    accessToken ? { accessToken } : undefined
  );

export const deleteUserSession = async (
  userId: string,
  sessionId: number,
  accessToken?: string
) =>
  gatewayDelete<void>(
    `/api/gateway/user/${userId}/sessions/${sessionId}`,
    undefined,
    accessToken ? { accessToken } : undefined
  );

export const fetchChatHistory = async (
  userId: string,
  sessionId: number,
  accessToken?: string
) =>
  gatewayGet<GatewayChatHistoryEntry[]>(
    `/api/gateway/user/${userId}/sessions/${sessionId}/history`,
    undefined,
    accessToken ? { accessToken } : undefined
  );

export const getUserProfileViaGateway = async (
  userId: string,
  session?: GatewaySession
) =>
  gatewayGet<GatewayUserProfile>(
    `/api/gateway/user/${userId}/profile`,
    undefined,
    session
  );

export const updateNicknameViaGateway = async (
  userId: string,
  payload: { nickname: string },
  session?: GatewaySession
) =>
  gatewayPost<GatewayUserProfile, typeof payload>(
    `/api/gateway/user/${userId}/nickname`,
    payload,
    undefined,
    session
  );

export const extractGatewayError = (
  error: unknown
): GatewayErrorResponse | null => {
  if (error instanceof AxiosError) {
    return error.response?.data as GatewayErrorResponse;
  }
  return null;
};

export const streamGatewayChat = async (
  payload: {
    id: string;
    message: {
      id: string;
      role: "user";
      parts: Array<{ type: "text"; text: string } | { type: "file"; mediaType: string; name: string; url: string }>;
    };
    selectedChatModel: string;
    selectedVisibilityType: string;
    sessionId?: string;
    sessionTitle?: string;
    history?: Array<{ role: "user" | "assistant"; content: string }>;
    userId: number;
    clientIp?: string;
  },
  accessToken: string
): Promise<AxiosResponse<ReadableStream<Uint8Array>>> => {
  const response = await gatewayClient.post(
    "/api/gateway/qa/chat",
    {
      sessionId: payload.sessionId,
      sessionTitle: payload.sessionTitle,
      userId: payload.userId,
      question: payload.message.parts
        .filter((part) => part.type === "text")
        .map((part) => ("text" in part ? part.text : ""))
        .join(" "),
      history: payload.history || [],
    },
    {
      headers: {
        Authorization: `Bearer ${accessToken}`,
        Accept: "text/event-stream",
      },
      responseType: "stream",
    }
  );

  return response;
};

export { gatewayClient };
