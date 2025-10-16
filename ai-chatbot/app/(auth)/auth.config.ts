import type { NextAuthConfig } from "next-auth";
import type { UserType } from "./auth";

export const authConfig = {
  pages: {
    signIn: "/login",
    newUser: "/",
  },
  session: {
    strategy: "jwt",
    maxAge: 60 * 60 * 12,
  },
  trustHost: true, // 信任代理主机，解决部署时的 UntrustedHost 错误
  // 重要：如果生产环境使用 HTTP（非 HTTPS），必须保持 useSecureCookies 为 false
  // 否则 Cookie 无法设置，导致登录循环
  useSecureCookies: false, // 暂时禁用安全 cookies，支持 HTTP 部署
  cookies: {
    sessionToken: {
      // 生产环境如果使用 HTTP，不能使用 __Secure- 前缀
      name: "next-auth.session-token", // 统一使用普通 cookie 名称
      options: {
        httpOnly: true,
        sameSite: "lax",
        path: "/",
        secure: false, // HTTP 环境必须设置为 false
        domain: process.env.NODE_ENV === "production" ? undefined : undefined,
      },
    },
    callbackUrl: {
      name: `next-auth.callback-url`,
      options: {
        httpOnly: true,
        sameSite: "lax",
        path: "/",
        secure: false, // HTTP 环境必须设置为 false
      },
    },
    csrfToken: {
      name: `next-auth.csrf-token`,
      options: {
        httpOnly: true,
        sameSite: "lax",
        path: "/",
        secure: false, // HTTP 环境必须设置为 false
      },
    },
  },
  providers: [
    // added later in auth.ts since it requires bcrypt which is only compatible with Node.js
    // while this file is also used in non-Node.js environments
  ],
} satisfies NextAuthConfig;
