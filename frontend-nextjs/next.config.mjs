/** @type {import('next').NextConfig} */
const nextConfig = {
  optimizeFonts: false,           // 禁用字体优化
  eslint: {
    ignoreDuringBuilds: true,
  },
  typescript: {
    ignoreBuildErrors: true,
  },
  images: {
    unoptimized: true,
  },
}

export default nextConfig
