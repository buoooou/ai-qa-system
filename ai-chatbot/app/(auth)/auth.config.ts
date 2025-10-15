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
  useSecureCookies: process.env.NODE_ENV === "production", // 仅在生产环境使用安全 cookies
  providers: [
    // added later in auth.ts since it requires bcrypt which is only compatible with Node.js
    // while this file is also used in non-Node.js environments
  ],
} satisfies NextAuthConfig;
