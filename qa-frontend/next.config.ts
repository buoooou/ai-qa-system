import type { NextConfig } from "next";

const nextConfig: NextConfig = {
 eslint: {
     ignoreDuringBuilds: true, // ← build 时跳过 ESLint
   },
   typescript: {
     ignoreBuildErrors: true, // ← 跳过 TS 错误（可选，先救命用）
   },
};

export default nextConfig;
