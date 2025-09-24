'use client';

import React, { useState, useEffect } from 'react';
import LoginPage from '@/components/LoginPage';
import RegisterPage from '@/components/RegisterPage';
import ChatInterface from '@/components/ChatInterface';
import { User } from '@/services/api';

type AppState = 'loading' | 'login' | 'register' | 'chat';

export default function Home() {
  const [appState, setAppState] = useState<AppState>('loading');
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);

  // 检查本地存储的登录状态
  useEffect(() => {
    const checkAuthStatus = () => {
      try {
        const savedToken = localStorage.getItem('token');
        const savedUser = localStorage.getItem('user');
        
        if (savedToken && savedUser) {
          const parsedUser = JSON.parse(savedUser);
          setToken(savedToken);
          setUser(parsedUser);
          setAppState('chat');
        } else {
          setAppState('login');
        }
      } catch (error) {
        console.error('Failed to parse saved user data:', error);
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setAppState('login');
      }
    };

    checkAuthStatus();
  }, []);

  // 登录成功处理
  const handleLoginSuccess = (userData: User, userToken: string) => {
    setUser(userData);
    setToken(userToken);
    setAppState('chat');
  };

  // 注册成功处理
  const handleRegisterSuccess = () => {
    setAppState('login');
  };

  // 退出登录处理
  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
    setToken(null);
    setAppState('login');
  };

  // 切换到注册页面
  const handleSwitchToRegister = () => {
    setAppState('register');
  };

  // 切换到登录页面
  const handleSwitchToLogin = () => {
    setAppState('login');
  };

  // 加载状态
  if (appState === 'loading') {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100">
        <div className="text-center">
          <div className="mx-auto h-16 w-16 bg-indigo-600 rounded-full flex items-center justify-center mb-4">
            <svg className="animate-spin h-8 w-8 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
          </div>
          <h2 className="text-xl font-semibold text-gray-900 mb-2">
            AI智能问答系统
          </h2>
          <p className="text-gray-600">正在加载中...</p>
        </div>
      </div>
    );
  }

  // 根据应用状态渲染不同组件
  switch (appState) {
    case 'login':
      return (
        <LoginPage
          onLoginSuccess={handleLoginSuccess}
          onSwitchToRegister={handleSwitchToRegister}
        />
      );
    
    case 'register':
      return (
        <RegisterPage
          onRegisterSuccess={handleRegisterSuccess}
          onSwitchToLogin={handleSwitchToLogin}
        />
      );
    
    case 'chat':
      return user ? (
        <ChatInterface
          user={user}
          onLogout={handleLogout}
        />
      ) : (
        <div className="min-h-screen flex items-center justify-center">
          <div className="text-center">
            <h2 className="text-xl font-semibold text-gray-900 mb-2">
              用户信息加载失败
            </h2>
            <button
              onClick={handleLogout}
              className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700"
            >
              重新登录
            </button>
          </div>
        </div>
      );
    
    default:
      return (
        <div className="min-h-screen flex items-center justify-center">
          <div className="text-center">
            <h2 className="text-xl font-semibold text-gray-900 mb-2">
              应用状态错误
            </h2>
            <button
              onClick={() => setAppState('login')}
              className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700"
            >
              返回登录
            </button>
          </div>
        </div>
      );
  }
}