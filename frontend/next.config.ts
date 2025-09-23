import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // 启用standalone输出模式，用于Docker部署
  output: 'standalone',
  
  async rewrites() {
    return [
      {
        source: '/api/users/:path*',
        destination: 'http://localhost:8080/api/users/:path*',
      },
      {
        source: '/api/qa/:path*',
        destination: 'http://localhost:8080/api/qa/:path*',
      },
    ];
  },
};

export default nextConfig;
