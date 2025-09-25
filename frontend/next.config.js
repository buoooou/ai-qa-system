/** @type {import('next').NextConfig} */
 const nextConfig = {
    reactStrictMode: true,
    async rewrites() {
      return [
        {
          source: '/api/:path*',
          destination: 'http://localhost:8080/api/:path*', // 保持完整的API路径
        },
      ];
    },
  };

  module.exports = nextConfig;
