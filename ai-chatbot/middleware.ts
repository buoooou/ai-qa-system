import { type NextRequest, NextResponse } from "next/server";
import { getToken } from "next-auth/jwt";
import { isDevelopmentEnvironment } from "./lib/constants";
import { sanitizeRedirectTarget } from "./lib/redirect";

export async function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  /*
   * Playwright starts the dev server and requires a 200 status to
   * begin the tests, so this ensures that the tests can start
   */
  if (pathname.startsWith("/ping")) {
    return new Response("pong", { status: 200 });
  }

  if (pathname.startsWith("/api/auth")) {
    return NextResponse.next();
  }

  const isAuthPage = pathname === "/login" || pathname === "/register";

  let token: Awaited<ReturnType<typeof getToken>> | null = null;
  try {
    token = await getToken({
      req: request,
      secret: process.env.AUTH_SECRET,
      secureCookie: false, // 强制使用不安全的 cookies（HTTP）
    });
  } catch (error) {
    console.error("middleware: failed to read session token", error);
  }

  if (!token) {
    if (isAuthPage) {
      return NextResponse.next();
    }

    const safeRedirectPath = sanitizeRedirectTarget(
      request.nextUrl.pathname + request.nextUrl.search + request.nextUrl.hash,
      request.nextUrl
    );

    const loginUrl = new URL("/login", request.url);

    if (safeRedirectPath !== "/") {
      loginUrl.searchParams.set("redirect", safeRedirectPath);
    }

    return NextResponse.redirect(loginUrl);
  }

  if (token && isAuthPage) {
    return NextResponse.redirect(new URL("/", request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: [
    "/",
    "/chat/:id",
    "/api/:path*",
    "/login",
    "/register",

    /*
     * Match all request paths except for the ones starting with:
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico, sitemap.xml, robots.txt (metadata files)
     */
    "/((?!_next/static|_next/image|favicon.ico|sitemap.xml|robots.txt).*)",
  ],
};
