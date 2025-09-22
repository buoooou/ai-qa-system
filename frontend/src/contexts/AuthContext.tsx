'use client';

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';

interface User {
  id: string;
  name: string;
  email: string;
}

interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

// 预设用户数据
const PRESET_USERS = [
  { id: '1', name: 'Admin1', email: 'admin1@example.com', password: 'admin1' },
  { id: '2', name: 'Admin2', email: 'admin2@example.com', password: 'admin2' }
];

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // 检查本地存储中的认证信息
    const token = localStorage.getItem('authToken');
    const userData = localStorage.getItem('userData');
    
    if (token && userData) {
      try {
        const parsedUser = JSON.parse(userData);
        setUser(parsedUser);
      } catch (e) {
        // 如果解析失败，清除本地存储
        localStorage.removeItem('authToken');
        localStorage.removeItem('userData');
      }
    }
    setLoading(false);
  }, []);

  const login = async (email: string, password: string) => {
    // 模拟API调用
    return new Promise<void>((resolve, reject) => {
      setTimeout(() => {
        // 检查预设用户
        const presetUser = PRESET_USERS.find(
          user => user.email === email && user.password === password
        );
        
        if (presetUser) {
          const userData = {
            id: presetUser.id,
            name: presetUser.name,
            email: presetUser.email
          };
          // 保存认证信息到本地存储
          localStorage.setItem('authToken', 'fake-jwt-token');
          localStorage.setItem('userData', JSON.stringify(userData));
          setUser(userData);
          resolve();
        } else if (email === 'user@example.com' && password === 'password') {
          const userData = {
            id: '3',
            name: '测试用户',
            email: email
          };
          // 保存认证信息到本地存储
          localStorage.setItem('authToken', 'fake-jwt-token');
          localStorage.setItem('userData', JSON.stringify(userData));
          setUser(userData);
          resolve();
        } else {
          reject(new Error('邮箱或密码错误'));
        }
      }, 500);
    });
  };

  const register = async (name: string, email: string, password: string) => {
    // 模拟API调用
    return new Promise<void>((resolve, reject) => {
      setTimeout(() => {
        // 检查用户是否已存在（包括预设用户）
        const existingUser = [...PRESET_USERS, { email: 'user@example.com' }].find(
          user => user.email === email
        );
        
        if (existingUser) {
          reject(new Error('用户已存在'));
        } else {
          const userData = {
            id: Date.now().toString(),
            name: name,
            email: email
          };
          // 保存认证信息到本地存储
          localStorage.setItem('authToken', 'fake-jwt-token');
          localStorage.setItem('userData', JSON.stringify(userData));
          setUser(userData);
          resolve();
        }
      }, 500);
    });
  };

  const logout = () => {
    // 清除本地存储的认证信息
    localStorage.removeItem('authToken');
    localStorage.removeItem('userData');
    setUser(null);
  };

  const value = {
    user,
    loading,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}