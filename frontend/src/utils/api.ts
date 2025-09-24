import axios from 'axios';
import { ApiResponse, LoginRequest, LoginResponse, RegisterRequest, User, QuestionRequest, QuestionResponse } from '@/types';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const userApi = {
  login: (data: LoginRequest): Promise<ApiResponse<LoginResponse>> =>
    api.post('/users/login', data).then(res => res.data),

  register: (data: RegisterRequest): Promise<ApiResponse<User>> =>
    api.post('/users/register', data).then(res => res.data),

  getUserById: (id: number): Promise<ApiResponse<User>> =>
    api.get(`/users/${id}`).then(res => res.data),

  updateNickname: (userId: number, nickname: string): Promise<ApiResponse<User>> =>
    api.put(`/users/${userId}/nickname`, { nickname }).then(res => res.data),
};

export const qaApi = {
  askQuestion: (data: QuestionRequest): Promise<ApiResponse<QuestionResponse>> =>
    api.post('/qa/question', data).then(res => res.data),

  getUserHistory: (userId: number, page = 0, size = 10): Promise<ApiResponse<QuestionResponse[]>> =>
    api.get(`/qa/history/user/${userId}?page=${page}&size=${size}`).then(res => res.data),

  getSessionHistory: (sessionId: string): Promise<ApiResponse<QuestionResponse[]>> =>
    api.get(`/qa/history/session/${sessionId}`).then(res => res.data),

  deleteQuestion: (qaId: number): Promise<ApiResponse<void>> =>
    api.delete(`/qa/${qaId}`).then(res => res.data),
};

export default api;