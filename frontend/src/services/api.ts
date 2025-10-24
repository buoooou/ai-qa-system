import axios from 'axios';

// API基础配置
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

// 创建axios实例
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 120000, // 120秒超时（匹配后端Gemini API响应时间）
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    // 可以在这里添加认证token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    console.log('API Request:', config.method?.toUpperCase(), config.url);
    return config;
  },
  (error) => {
    console.error('Request Error:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    console.log('API Response:', response.status, response.config.url);
    return response;
  },
  (error) => {
    console.error('Response Error:', error.response?.status, error.response?.data);
    
    // 处理401未授权错误
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    
    return Promise.reject(error);
  }
);

// API响应类型定义
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
  success: boolean;
  error: boolean;
}

// 用户相关API
export interface User {
  id: number;
  userName: string;
  nickname: string;
  createTime: string;
}

export interface LoginRequest {
  userName: string;
  password: string;
}

export interface LoginResponse {
  user: User;
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface RegisterRequest {
  username: string;
  password: string;
  confirmPassword: string;
  nickname: string;
}

// 问答相关API
export interface QARequest {
  userId: number;
  question: string;
  includeHistory?: boolean;
}

export interface QAResponse {
  id: number;
  userId: number;
  question: string;
  answer: string;
  createTime: string;
  model?: string;
  responseTime?: number;
}

// 用户API服务
export const userApi = {
  // 用户登录
  login: async (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
    const response = await apiClient.post('/api/auth/login', data);
    return response.data;
  },

  // 用户注册
  register: async (data: RegisterRequest): Promise<ApiResponse<User>> => {
    const response = await apiClient.post('/api/user/register', data);
    return response.data;
  },

  // 获取用户信息
  getUserInfo: async (userId: number): Promise<ApiResponse<User>> => {
    const response = await apiClient.get(`/api/user/${userId}`);
    return response.data;
  },

  // 健康检查
  health: async (): Promise<ApiResponse<string>> => {
    const response = await apiClient.get('/api/user/health');
    return response.data;
  },
};

// 问答API服务
export const qaApi = {
  // 提交问题
  askQuestion: async (data: QARequest): Promise<ApiResponse<QAResponse>> => {
    const response = await apiClient.post('/api/qa/ask', data);
    return response.data;
  },

  // 获取用户问答历史
  getUserHistory: async (userId: number): Promise<ApiResponse<QAResponse[]>> => {
    const response = await apiClient.get(`/api/qa/history/${userId}`);
    return response.data;
  },

  // 分页获取用户问答历史
  getUserHistoryPaged: async (
    userId: number,
    page: number = 0,
    size: number = 10
  ): Promise<ApiResponse<QAResponse[]>> => {
    const response = await apiClient.get(`/api/qa/history/${userId}/paged`, {
      params: { page, size },
    });
    return response.data;
  },

  // 搜索问答历史
  searchHistory: async (userId: number, keyword: string): Promise<ApiResponse<QAResponse[]>> => {
    const response = await apiClient.get(`/api/qa/search/${userId}`, {
      params: { keyword },
    });
    return response.data;
  },

  // 删除问答记录
  deleteQaRecord: async (id: number, userId: number): Promise<ApiResponse<boolean>> => {
    const response = await apiClient.delete(`/api/qa/${id}`, {
      params: { userId },
    });
    return response.data;
  },

  // 获取问答总数
  getUserQaCount: async (userId: number): Promise<ApiResponse<number>> => {
    const response = await apiClient.get(`/api/qa/count/${userId}`);
    return response.data;
  },

  // 健康检查
  // health: async (): Promise<ApiResponse<string>> => {
  //   const response = await apiClient.get('/api/qa/health');
  //   return response.data;
  // },

  // 获取服务信息
  getServiceInfo: async (): Promise<ApiResponse<any>> => {
    const response = await apiClient.get('/api/qa/info');
    return response.data;
  },
};

// 导出默认的API客户端
export default apiClient;
