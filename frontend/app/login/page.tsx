"use client";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import toast from "react-hot-toast";
import axios from "../commons/axios";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();

  // 调试 Toaster 状态
  useEffect(() => {
    console.log("Login 组件挂载");
    // 清除非法退出时未清空的 localStorage
    localStorage.clear();
    return () => {
      console.log("Login 组件卸载");
    };
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim()) {
      toast.error("请输入用户名");
      return;
    }
    if (!password.trim()) {
      toast.error("请输入密码");
      return;
    }

    setIsLoading(true);
    try {
      // const response = await fetch('/api/user/login', {
      //   method: 'POST',
      //   headers: { 'Content-Type': 'application/json' },
      //   body: JSON.stringify({ username, password }),
      // });

      console.log("333333333333");
      const response = await axios.post("/api/user/login", {
        username: username,
        password: password,
      });
      console.log(response);
      console.log("44444444444");
      if (response.data.code !== 200) {
        console.log("132132213");
        throw new Error(`登录失败: ${response.data.message}`);
      }

      toast.success("登录成功");
      localStorage.setItem("username", username);
      localStorage.setItem("userid", response.data.data.userId);
      localStorage.setItem("token", response.data.data.token);

      await new Promise((resolve) => setTimeout(resolve, 1500));
      router.push("/chat");
    } catch (error) {
      toast.error(`登录失败`);
      console.error("登录接口错误:", error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background relative">
      {/* 全屏蒙版 - 登录中显示 */}
      {isLoading && (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50">
          <div className="text-center">
            <div className="inline-block h-12 w-12 border-4 border-primary-color border-t-transparent rounded-full animate-spin mb-4"></div>
            <p className="text-foreground text-lg font-medium">正在登录，请稍候...</p>
          </div>
        </div>
      )}
      
      <div className="max-w-md w-full space-y-8 p-8 bg-card-bg rounded-xl shadow-lg border border-border-color">
        {/* 头部 */}
        <div className="flex items-center justify-center gap-3">
          <div className="h-12 w-12 bg-primary-color rounded-full flex items-center justify-center">
            <svg
              className="h-6 w-6 text-white"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
              />
            </svg>
          </div>
          <h2 className="text-3xl font-bold text-foreground">
            智能助手系统
          </h2>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-foreground mb-1">
              用户名
            </label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full p-2 border border-border-color bg-background text-foreground rounded-md focus:outline-none focus:ring-2 focus:ring-primary-color"
              placeholder="请输入用户名"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-foreground mb-1">
              密码
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full p-2 border border-border-color bg-background text-foreground rounded-md focus:outline-none focus:ring-2 focus:ring-primary-color"
              placeholder="请输入密码"
            />
          </div>
          <button
            type="submit"
            disabled={isLoading}
            className="w-full bg-primary-color text-white py-2 px-4 rounded-md hover:bg-primary-hover focus:outline-none focus:ring-2 focus:ring-primary-color focus:ring-offset-2 disabled:opacity-50"
          >
            {isLoading ? "登录中..." : "登录"}
          </button>
        </form>
        <div className="mt-4 text-center">
          <span className="text-sm text-foreground/70">没有账号？</span>
          <a href="/register" className="text-sm text-primary-color hover:underline">
            立即注册
          </a>
        </div>
      </div>
    </div>
  );
}
