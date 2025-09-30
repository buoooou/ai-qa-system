import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "智能助手系统 - AI问答平台",
  description: "专业的AI智能问答系统，提供精准高效的智能对话服务",
};

import { Toaster } from "react-hot-toast";

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body
        className="antialiased"
      >
        {children}
        <Toaster
          toastOptions={{
            error: {
              style: {
                background: "#ff4d4f",
                color: "#fff",
                borderRadius: "8px",
                boxShadow: "0 4px 12px rgba(0, 0, 0, 0.15)",
                padding: "16px",
                fontSize: "14px",
                animation: "fadeIn 0.3s ease-out",
              },
              iconTheme: {
                primary: "#fff",
                secondary: "#ff4d4f",
              },
            },
          }}
        />
      </body>
    </html>
  );
}
