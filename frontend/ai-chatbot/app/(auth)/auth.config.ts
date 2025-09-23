import type { NextAuthConfig } from 'next-auth';

export const authConfig = {
  pages: {
    signIn: '/login',
    newUser: '/',
  },
  providers: [
    // added later in auth.ts since it requires bcrypt which is only compatible with Node.js
    // while this file is also used in non-Node.js environments
  ],
  callbacks: {},
    // 关键配置：添加信任的主机
  trustedHosts: {
    // 允许 localhost:3000（开发环境）
    localhost: ['localhost:3000'],
    // 允许你的服务器 IP:3000（生产环境，根据实际情况修改）
    '3.27.35.21': ['3.27.35.21:3000'],
    // 如果有域名，也可以添加，例如：
    // 'your-domain.com': ['your-domain.com', 'www.your-domain.com'],
  },
} satisfies NextAuthConfig;
