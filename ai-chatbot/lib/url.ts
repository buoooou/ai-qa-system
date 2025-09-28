const FALLBACK_REDIRECT = "/";

const HOSTNAME_REGEX = /^[a-z0-9.-]+$/i;

export function toSameOriginPath(
  target: string | null | undefined,
  current: URL
): string {
  if (!target) {
    return FALLBACK_REDIRECT;
  }

  let parsed: URL;

  try {
    parsed = new URL(target, current);
  } catch (error) {
    console.warn("Failed to parse redirect", { target, error });
    return FALLBACK_REDIRECT;
  }

  if (parsed.origin !== current.origin) {
    console.warn("Rejected cross-origin redirect", {
      requested: parsed.origin,
      current: current.origin,
    });
    return FALLBACK_REDIRECT;
  }

  if (!HOSTNAME_REGEX.test(parsed.hostname)) {
    console.warn("Rejected suspicious hostname in redirect", {
      hostname: parsed.hostname,
    });
    return FALLBACK_REDIRECT;
  }

  parsed.searchParams.delete("redirect");

  return parsed.pathname + parsed.search + parsed.hash;
}

export function buildRedirectUrl(base: URL, nextPath: string): URL {
  const redirect = toSameOriginPath(nextPath, base);
  const result = new URL(base);
  result.search = "";
  result.hash = "";
  result.pathname = redirect;
  return result;
}
