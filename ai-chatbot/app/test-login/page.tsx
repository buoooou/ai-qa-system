"use client";

import { getSession, signIn } from "next-auth/react";
import { useRouter } from "next/navigation";
import { useState, useEffect } from "react";

export default function TestLoginPage() {
  const router = useRouter();
  const [session, setSession] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    // 获取当前 session
    getSession().then((s) => {
      console.log("[TestLogin] Current session:", s);
      setSession(s);
    });
  }, []);

  const handleDirectLogin = async () => {
    setLoading(true);
    setError("");

    try {
      console.log("[TestLogin] Attempting direct signIn...");
      const result = await signIn("credentials", {
        email: "demo@cn.ibm.com",
        password: "password123",
        redirect: false,
      });

      console.log("[TestLogin] SignIn result:", result);

      if (result?.error) {
        setError(`Login failed: ${result.error}`);
      } else if (result?.ok) {
        console.log("[TestLogin] Login successful, refreshing session...");
        // 刷新 session
        const newSession = await getSession();
        console.log("[TestLogin] New session:", newSession);
        setSession(newSession);

        // 手动跳转
        window.location.href = "/";
      }
    } catch (err) {
      console.error("[TestLogin] Login error:", err);
      setError(`Error: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: "20px", maxWidth: "600px", margin: "0 auto" }}>
      <h1>Test Login Page</h1>

      <div style={{ marginBottom: "20px" }}>
        <h2>Current Session:</h2>
        <pre style={{ background: "#f5f5f5", padding: "10px", overflow: "auto" }}>
          {JSON.stringify(session, null, 2)}
        </pre>
      </div>

      <button
        onClick={handleDirectLogin}
        disabled={loading}
        style={{
          padding: "10px 20px",
          fontSize: "16px",
          backgroundColor: loading ? "#ccc" : "#007bff",
          color: "white",
          border: "none",
          borderRadius: "5px",
          cursor: loading ? "not-allowed" : "pointer"
        }}
      >
        {loading ? "Logging in..." : "Login with demo@cn.ibm.com"}
      </button>

      {error && (
        <div style={{ marginTop: "20px", color: "red", backgroundColor: "#ffe6e6", padding: "10px" }}>
          <strong>Error:</strong> {error}
        </div>
      )}

      <div style={{ marginTop: "30px" }}>
        <h2>Environment Info:</h2>
        <ul>
          <li>NEXT_PUBLIC_GATEWAY_URL: {process.env.NEXT_PUBLIC_GATEWAY_URL || "Not set"}</li>
          <li>NODE_ENV: {process.env.NODE_ENV || "Not set"}</li>
        </ul>
      </div>

      <div style={{ marginTop: "20px" }}>
        <a href="/" style={{ color: "#007bff" }}>Go to Home</a> |
        <a href="/login" style={{ color: "#007bff", marginLeft: "10px" }}>Go to Login</a>
      </div>
    </div>
  );
}