"use client";

import { useSession } from "next-auth/react";
import { useRouter, usePathname } from "next/navigation";
import { useEffect } from "react";

interface ChatWrapperProps {
  children: React.ReactNode;
}

export function ChatWrapper({ children }: ChatWrapperProps) {
  const { data: session, status } = useSession();
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    // Skip check if still loading
    if (status === 'loading') return;

    // If logged in but missing accessToken, redirect to login
    if (status === 'authenticated' && session?.user && !session.user.accessToken) {
      console.log("No accessToken found in chat wrapper, redirecting to login", {
        hasUser: !!session.user,
        userKeys: session.user ? Object.keys(session.user) : []
      });
      router.push('/login');
      return;
    }
  }, [session, status, router]);

  return <>{children}</>;
}