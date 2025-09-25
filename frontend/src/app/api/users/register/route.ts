import { NextRequest } from 'next/server';

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const { name, email, password } = body;

    // 转换前端请求格式到后端格式
    const backendRequest = {
      username: email, // 后端使用username字段
      password: password,
      nick: name // 后端使用nick字段
    };

    // 转发请求到后端API网关（使用Docker服务名）
    const response = await fetch('http://api-gateway:8080/api/users/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(backendRequest),
    });

    const data = await response.json();

    if (!response.ok) {
      return new Response(
        JSON.stringify({ error: data.message || '注册失败' }),
        { status: response.status, headers: { 'Content-Type': 'application/json' } }
      );
    }

    // 转换后端响应格式到前端格式
    if (data.result === 'SUCCESS') {
      return new Response(
        JSON.stringify({ 
          message: '注册成功',
          user: {
            id: data.data.id,
            name: data.data.nick,
            email: data.data.username
          },
          token: 'JWT_TOKEN_PLACEHOLDER' // 临时token
        }),
        { status: 201, headers: { 'Content-Type': 'application/json' } }
      );
    } else {
      return new Response(
        JSON.stringify({ error: data.message || '注册失败' }),
        { status: 400, headers: { 'Content-Type': 'application/json' } }
      );
    }
  } catch (error) {
    console.error('注册代理错误:', error);
    return new Response(
      JSON.stringify({ error: '服务器内部错误' }),
      { status: 500, headers: { 'Content-Type': 'application/json' } }
    );
  }
}