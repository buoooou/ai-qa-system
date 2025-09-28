const FALLBACK_PATH = "/";

const LOOP_GUARD_PATHS = ["/login", "/register"] as const;

function isLoopGuardPath(pathname: string): boolean {
  return LOOP_GUARD_PATHS.some(
    (guard) => pathname === guard || pathname.startsWith(`${guard}/`)
  );
}

export function sanitizeRedirectTarget(
  candidate: string | null | undefined,
  base: URL
): string {
  if (!candidate) {
    return FALLBACK_PATH;
  }

  let parsed: URL;

  try {
    parsed = new URL(candidate, base);
  } catch (error) {
    console.warn("sanitizeRedirectTarget: failed to parse candidate", {
      candidate,
      error,
    });
    return FALLBACK_PATH;
  }

  if (parsed.origin !== base.origin) {
    console.warn("sanitizeRedirectTarget: rejected cross-origin redirect", {
      requested: parsed.origin,
      base: base.origin,
    });
    return FALLBACK_PATH;
  }

  parsed.searchParams.delete("redirect");

  if (isLoopGuardPath(parsed.pathname)) {
    return FALLBACK_PATH;
  }

  const path = `${parsed.pathname}${parsed.search}${parsed.hash}`;

  if (!path.startsWith("/")) {
    return FALLBACK_PATH;
  }

  return path === "" ? FALLBACK_PATH : path;
}
