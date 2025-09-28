import { http, HttpResponse } from 'msw';

const API_BASE_URL = 'http://localhost:8083';

const mockProfile = {
  id: 1,
  username: 'testuser',
  email: 'test@example.com',
  nickname: 'Test User',
  role: 'USER',
};

export const handlers = [
  // 登录接口
  http.post(`${API_BASE_URL}/api/gateway/auth/login`, async ({ request }) => {
    const body = await request.json();
    
    if (body.usernameOrEmail === 'testuser' && body.password === 'password123') {
      return HttpResponse.json({
        token: 'mock-jwt-token',
        expiresIn: 3600,
        profile: mockProfile,
      });
    }
    
    return new HttpResponse('Invalid credentials', { status: 401 });
  }),

  // 注册接口
  http.post(`${API_BASE_URL}/api/gateway/auth/register`, async ({ request }) => {
    const body = await request.json();
    
    if (body.username && body.email && body.password) {
      return HttpResponse.json({
        token: 'mock-jwt-token',
        expiresIn: 3600,
        profile: {
          ...mockProfile,
          username: body.username,
          email: body.email,
          nickname: body.nickname,
        },
      });
    }
    
    return new HttpResponse('Invalid registration data', { status: 400 });
  }),

  // 获取用户信息接口
  http.get(`${API_BASE_URL}/api/user/1/profile`, ({ request }) => {
    const authHeader = request.headers.get('Authorization');
    
    if (authHeader === 'Bearer mock-jwt-token') {
      return HttpResponse.json(mockProfile);
    }
    
    return new HttpResponse('Unauthorized', { status: 401 });
  }),
];
