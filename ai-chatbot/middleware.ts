import { type NextRequest, NextResponse } from "next/server";
import { getToken } from "next-auth/jwt";
import { sanitizeRedirectTarget } from "./lib/redirect";

export async function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  console.log("[Middleware] Processing request:", { pathname });

  /*
   * Playwright starts the dev server and requires a 200 status to
   * begin the tests, so this ensures that the tests can start
   */
  if (pathname.startsWith("/ping")) {
    return new Response("pong", { status: 200 });
  }

  if (pathname.startsWith("/api/auth")) {
    console.log("[Middleware] API auth route, allowing...");
    return NextResponse.next();
  }

  const isAuthPage = pathname === "/login" || pathname === "/register";

  let token: Awaited<ReturnType<typeof getToken>> | null = null;
  try {
    // 统一使用普通 cookie，支持 HTTP 部署
    token = await getToken({
      req: request,
      secret: process.env.AUTH_SECRET,
      secureCookie: false, // HTTP 环境必须设置为 false
      cookieName: "next-auth.session-token",
    });
    console.log("[Middleware] Token result:", {
      hasToken: !!token,
      userId: token?.id,
      pathname
    });
  } catch (error) {
    console.error("[Middleware] Failed to read session token:", error);
  }

  if (!token) {
    console.log("[Middleware] No token found");

    if (isAuthPage) {
      console.log("[Middleware] Auth page without token, allowing...");
      return NextResponse.next();
    }

    console.log("[Middleware] Redirecting to login...");
    const loginUrl = new URL("/login", request.url);

    // 不添加 redirect 参数，避免复杂的重定向链
    return NextResponse.redirect(loginUrl);
  }

  // 如果有 token 且在认证页面，重定向到首页
  if (token && isAuthPage) {
    console.log("[Middleware] Has token and on auth page, redirecting to home...");
    return NextResponse.redirect(new URL("/", request.url));
  }

  console.log("[Middleware] Allowing request to proceed...");
  return NextResponse.next();
}

export const config = {
  matcher: [
    /*
     * Match all request paths except for the ones starting with:
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico, sitemap.xml, robots.txt (metadata files)
     */
    "/((?!_next/static|_next/image|favicon.ico|sitemap.xml|robots.txt|ping).*)",
  ],
};
