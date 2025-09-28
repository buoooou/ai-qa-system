import type { NextAuthConfig } from "next-auth";

export const authConfig = {
  pages: {
    signIn: "/login",
    newUser: "/",
  },
  session: {
    strategy: "jwt",
    maxAge: 60 * 60 * 12,
  },
  providers: [
    // added later in auth.ts since it requires bcrypt which is only compatible with Node.js
    // while this file is also used in non-Node.js environments
  ],
  callbacks: {
    session({ session, token }) {
      if (session.user) {
        session.user.id = token.sub ?? "";
        session.user.type = (token as { type?: string }).type ?? "guest";
        session.user.accessToken = (
          token as { accessToken?: string }
        ).accessToken;
        session.user.role = (token as { role?: string }).role;
        session.user.username = (token as { username?: string }).username;
        session.user.nickname = (
          token as { nickname?: string | null }
        ).nickname;
        session.user.email = token.email ?? session.user.email;
      }

      return session;
    },
    jwt({ token, user }) {
      if (user) {
        token.sub = user.id ?? token.sub;
        token.id = user.id ?? token.id;
        token.type = (user as { type?: string }).type ?? "guest";
        token.accessToken = (user as { accessToken?: string }).accessToken;
        token.role = (user as { role?: string }).role;
        token.username = (user as { username?: string }).username;
        token.nickname = (user as { nickname?: string | null }).nickname;
      }
      return token;
    },
  },
} satisfies NextAuthConfig;
