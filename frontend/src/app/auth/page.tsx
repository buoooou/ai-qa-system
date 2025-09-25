'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';

export default function AuthPage() {
  const [isLogin, setIsLogin] = useState(true);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const { user, login, register, loading } = useAuth();
  const router = useRouter();

  // 如果用户已经登录，则重定向到聊天页面
  useEffect(() => {
    if (user) {
      router.push('/chat');
    }
  }, [user, router]);

  const validateForm = () => {
    if (!isLogin && name.trim() === '') {
      setError('请输入姓名');
      return false;
    }
    if (email.trim() === '') {
      setError('请输入邮箱');
      return false;
    }
    if (password.length < 6) {
      setError('密码至少需要6个字符');
      return false;
    }
    if (!isLogin && password !== confirmPassword) {
      setError('两次输入的密码不一致');
      return false;
    }
    setError('');
    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;

    try {
      if (isLogin) {
        await login(email, password);
      } else {
        await register(name, email, password);
      }
      router.push('/chat');
    } catch (err) {
      setError('操作失败，请检查您的信息');
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center p-4">
      <div className="w-full max-w-md rounded-lg bg-white p-8 shadow-md">
        <h1 className="mb-6 text-center text-2xl font-bold">
          {isLogin ? '登录' : '注册'}
        </h1>

        {error && (
          <div className="mb-4 rounded bg-red-100 p-3 text-red-700">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          {!isLogin && (
            <div className="mb-4">
              <label htmlFor="name" className="mb-2 block font-medium">
                姓名
              </label>
              <input
                id="name"
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
                required
              />
            </div>
          )}

          <div className="mb-4">
            <label htmlFor="email" className="mb-2 block font-medium">
              邮箱
            </label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
              required
            />
          </div>

          <div className="mb-4">
            <label htmlFor="password" className="mb-2 block font-medium">
              密码
            </label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
              required
            />
          </div>

          {!isLogin && (
            <div className="mb-6">
              <label htmlFor="confirmPassword" className="mb-2 block font-medium">
                确认密码
              </label>
              <input
                id="confirmPassword"
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
                required
              />
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full rounded bg-blue-500 py-2 font-medium text-white hover:bg-blue-600 disabled:opacity-50"
          >
            {loading ? '处理中...' : isLogin ? '登录' : '注册'}
          </button>
        </form>

        <p className="mt-4 text-center text-sm text-gray-600">
          {isLogin ? '还没有账户？' : '已有账户？'}
          <button
            type="button"
            onClick={() => {
              setIsLogin(!isLogin);
              setError('');
            }}
            className="ml-1 text-blue-500 hover:underline"
          >
            {isLogin ? '注册' : '登录'}
          </button>
        </p>
      </div>
    </div>
  );
}