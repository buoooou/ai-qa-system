import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  async rewrites() {
    return [
      {
        source: '/api/users/:path*',
        destination: 'http://localhost:8080/api/users/:path*',
      },
    ];
  },
};

export default nextConfig;
