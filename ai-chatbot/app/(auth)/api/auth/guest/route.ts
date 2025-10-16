import { NextResponse } from "next/server";

import { sanitizeRedirectTarget } from "@/lib/redirect";

export function GET(request: Request) {
  const { searchParams } = new URL(request.url);
  const referer = searchParams.get("redirectUrl") ?? "/";
  const safeRedirect = sanitizeRedirectTarget(referer, new URL(request.url));

  return NextResponse.redirect(new URL(safeRedirect, request.url));
}
