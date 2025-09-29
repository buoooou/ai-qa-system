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

      const response = await axios.post("/api/user/login", {
        username: username,
        password: password,
      });
      console.log(response);
      if (response.data.code !== 200) {
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
    <div className="min-h-screen flex items-center justify-center bg-slate-900">
      <div className="max-w-md w-full space-y-8 p-8 bg-slate-800 rounded-xl shadow-lg border border-slate-700 relative overflow-hidden">
        {/* 添加额外图标装饰 */}
        <div className="absolute -top-6 -right-6 h-24 w-24 opacity-10">
          <svg
            className="h-full w-full text-primary"
            fill="currentColor"
            viewBox="0 0 24 24"
          >
            <path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20zM7.5 13.5a1 1 0 1 1 0 2 1 1 0 0 1 0-2zm0-5a1 1 0 1 1 0 2 1 1 0 0 1 0-2zm9 5a1 1 0 1 1 0 2 1 1 0 0 1 0-2zm-3.5-5a1 1 0 1 1 0 2 1 1 0 0 1 0-2zm-2 6a3 3 0 1 1 6 0v1h-6v-1z"/>
          </svg>
        </div>
        
        {/* 头部 */}
        <div className="text-center relative z-10">
            <div className="flex items-center justify-center space-x-4 mb-2">
              <div className="h-12 w-12 bg-primary rounded-full flex items-center justify-center">
                <svg
                  className="h-6 w-6 text-white"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path 
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={1.5}
                    d="M12 4a4 4 0 1 1 0 8 4 4 0 0 1 0-8zM12 14c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zM5 19h14v-2H5v2zM5 7h14v2H5V7z"
                  />
                </svg>
              </div>
              <h2 className="text-3xl font-bold text-foreground">
                智能助手平台
              </h2>
            </div>
            <p className="text-sm text-foreground/70">
              欢迎登录，开启AI驱动的智能问答体验
            </p>
          </div>
        <form onSubmit={handleSubmit} className="space-y-4 relative z-10">
          <div>
            <label className="block text-sm font-medium text-foreground mb-1">
              账号名称
            </label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full p-2 bg-slate-700 border border-slate-600 rounded-md text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
              placeholder="请输入您的账号"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-foreground mb-1">
              登录密码
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full p-2 bg-slate-700 border border-slate-600 rounded-md text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
              placeholder="请输入您的密码"
            />
          </div>
          <button
            type="submit"
            disabled={isLoading}
            className="w-full bg-primary text-white py-2 px-4 rounded-md hover:bg-primary-variant focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2 disabled:opacity-50 transition-all duration-300"
          >
            {isLoading ? "正在登录..." : "立即登录"}
          </button>
        </form>
        <div className="mt-4 text-center relative z-10">
          <span className="text-sm text-foreground/80">还没有账号？</span>
          <a href="/register" className="text-sm text-primary hover:underline ml-1 transition-colors duration-200">
            立即注册新账号
          </a>
        </div>
      </div>
      
      {/* 全屏蒙板 - 登录请求处理中显示 */}
      {isLoading && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm z-50 flex items-center justify-center">
          <div className="bg-slate-800 p-6 rounded-lg shadow-2xl border border-slate-700 flex flex-col items-center">
            <div className="w-12 h-12 border-4 border-t-primary border-r-primary/50 border-b-primary/30 border-l-primary/70 rounded-full animate-spin mb-4"></div>
            <p className="text-lg font-medium text-foreground">正在登录系统...</p>
            <p className="text-sm text-foreground/70 mt-2">请稍候，正在验证您的身份</p>
          </div>
        </div>
      )}
    </div>
  );
}
