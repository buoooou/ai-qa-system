// import { NextResponse, type NextRequest } from 'next/server';
// import { getToken } from 'next-auth/jwt';
// import { guestRegex, isDevelopmentEnvironment } from './lib/constants';

// export async function middleware(request: NextRequest) {
//   const { pathname } = request.nextUrl;

//   /*
//    * Playwright starts the dev server and requires a 200 status to
//    * begin the tests, so this ensures that the tests can start
//    */
//   if (pathname.startsWith('/ping')) {
//     return new Response('pong', { status: 200 });
//   }

//   if (pathname.startsWith('/api/auth')) {
//     return NextResponse.next();
//   }

//   const token = await getToken({
//     req: request,
//     secret: process.env.AUTH_SECRET,
//     secureCookie: !isDevelopmentEnvironment,
//   });

//   if (!token) {
//     const redirectUrl = encodeURIComponent(request.url);

//     return NextResponse.redirect(
//       new URL(`/api/auth/guest?redirectUrl=${redirectUrl}`, request.url),
//     );
//   }

//   const isGuest = guestRegex.test(token?.email ?? '');

//   if (token && !isGuest && ['/login', '/register'].includes(pathname)) {
//     return NextResponse.redirect(new URL('/', request.url));
//   }

//   return NextResponse.next();
// }

// export const config = {
//   matcher: [
//     '/',
//     '/chat/:id',
//     '/api/:path*',
//     '/login',
//     '/register',

//     /*
//      * Match all request paths except for the ones starting with:
//      * - _next/static (static files)
//      * - _next/image (image optimization files)
//      * - favicon.ico, sitemap.xml, robots.txt (metadata files)
//      */
//     '/((?!_next/static|_next/image|favicon.ico|sitemap.xml|robots.txt).*)',
//   ],
// };
// ai-qa-system/frontend/ai-chatbot/middleware.ts
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';
// 关键修改：v4 版本用默认导入 getServerSession
import getServerSession from 'next-auth'; 
import { authConfig } from './app/(auth)/auth.config';

export async function middleware(req: NextRequest) {
  // 1. 排除无需登录的路由：登录页、认证接口、静态资源等
  const publicPaths = [
    '/login', // 登录页允许未登录访问
    '/api/auth/(.*)', // NextAuth 认证接口（如回调、登录）
    '/favicon.ico',
    '/_next/static',
    '/_next/image',
  ];

  // 检查当前请求是否在公共路径中
  const isPublicPath = publicPaths.some(path => 
    req.nextUrl.pathname.startsWith(path)
  );

  // 2. 公共路径直接放行
  if (isPublicPath) {
    return NextResponse.next();
  }

  // 3. 非公共路径：验证会话，无会话则跳登录页
  const session = await getServerSession(authConfig);
  if (!session) {
    // 重定向时携带原目标地址（登录后可跳回）
    return NextResponse.redirect(
      new URL(`/login?callbackUrl=${encodeURIComponent(req.nextUrl.pathname)}`, req.url)
    );
  }

  return NextResponse.next();
}

// 4. 明确匹配需要拦截的路由（仅拦截非公共路由）
export const config = {
  matcher: ['/((?!api/auth|login|_next/static|_next/image|favicon.ico).*)'],
};