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
  providers: [
    // added later in auth.ts since it requires bcrypt which is only compatible with Node.js
    // while this file is also used in non-Node.js environments
  ],
} satisfies NextAuthConfig;
