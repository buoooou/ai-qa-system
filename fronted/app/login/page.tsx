'use client';
import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();

  // 调试 Toaster 状态
  useEffect(() => {
    console.log('Login 组件挂载');
    // 清除非法退出时未清空的 localStorage
    localStorage.clear();
    return () => {
      console.log('Login 组件卸载');
    };
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim()) {
      toast.error('请输入用户名');
      return;
    }
    if (!password.trim()) {
      toast.error('请输入密码');
      return;
    }

    setIsLoading(true);
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });

      const data = await response.json();
      console.log("data", data);
      if (data.code !== 200) {
        throw new Error(`登录失败: ${data.message}`);
      }

      toast.success('登录成功');
      localStorage.setItem('userid', data.data.userid);
      localStorage.setItem('username', data.data.username);
      localStorage.setItem('nickname', data.data.nickname);
      localStorage.setItem('token', data.data.token);

      await new Promise(resolve => setTimeout(resolve, 1500));
      router.push('/chat');
    } catch (error: any) {
      toast.error( `${error.message}`);
      console.error('登录接口错误:', error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
        <h1 className="text-2xl font-bold mb-6 text-center">登录</h1>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">用户名</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="请输入用户名"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">密码</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="请输入密码"
            />
          </div>
          <button
            type="submit"
            disabled={isLoading}
            className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {isLoading ? '登录中...' : '登录'}
          </button>
        </form>
        <div className="mt-4 text-center">
          <span className="text-sm text-gray-600">没有账号？</span>
          <a href="/register" className="text-sm text-blue-600 hover:underline">立即注册</a>
        </div>
      </div>
    </div>
  );

}