import { NextRequest } from 'next/server';

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const { email, password } = body;

    // 转发请求到后端API网关，使用表单数据格式
    const formData = new URLSearchParams();
    formData.append('username', email);
    formData.append('password', password);

    const response = await fetch('http://api-gateway:8080/api/users/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: formData,
    });

    const data = await response.json();

    if (!response.ok) {
      return new Response(
        JSON.stringify({ error: data.message || '登录失败' }),
        { status: response.status, headers: { 'Content-Type': 'application/json' } }
      );
    }

    // 转换后端响应格式到前端格式
    if (data.result === 'SUCCESS') {
      return new Response(
        JSON.stringify({ 
          message: '登录成功',
          user: {
            id: data.data.user.id,
            name: data.data.user.nick,
            email: data.data.user.username
          },
          token: data.data.token || 'JWT_TOKEN_PLACEHOLDER'
        }),
        { status: 200, headers: { 'Content-Type': 'application/json' } }
      );
    } else {
      return new Response(
        JSON.stringify({ error: data.message || '登录失败' }),
        { status: 401, headers: { 'Content-Type': 'application/json' } }
      );
    }
  } catch (error) {
    console.error('登录代理错误:', error);
    return new Response(
      JSON.stringify({ error: '服务器内部错误' }),
      { status: 500, headers: { 'Content-Type': 'application/json' } }
    );
  }
}