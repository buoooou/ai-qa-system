"use client";

import { useEffect } from "react";

export function BodyCleaner() {
  useEffect(() => {
    // Remove any attributes added by browser extensions that might cause hydration issues
    const body = document.body;
    if (body && body.hasAttribute('ap-style')) {
      body.removeAttribute('ap-style');
    }
  }, []);

  return null;
}