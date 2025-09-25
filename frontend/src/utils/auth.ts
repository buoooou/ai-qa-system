import { User } from '@/types';

export const AuthUtils = {
  setToken: (token: string) => {
    localStorage.setItem('token', token);
  },

  getToken: (): string | null => {
    return localStorage.getItem('token');
  },

  removeToken: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  setUser: (user: User) => {
    localStorage.setItem('user', JSON.stringify(user));
  },

  getUser: (): User | null => {
    const userStr = localStorage.getItem('user');
    return userStr && userStr !== "undefined" ? JSON.parse(userStr) : null;
  },

  isAuthenticated: (): boolean => {
    return !!AuthUtils.getToken();
  },

  logout: () => {
    AuthUtils.removeToken();
    window.location.href = '/login';
  }
};