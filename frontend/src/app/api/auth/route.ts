import { NextRequest } from 'next/server';

// 模拟用户数据库
const users: { id: string; name: string; email: string; password: string }[] = [
  { id: '1', name: 'Admin1', email: 'admin1@example.com', password: 'admin1' },
  { id: '2', name: 'Admin2', email: 'admin2@example.com', password: 'admin2' }
];

export async function POST(request: NextRequest) {
  try {
    const { action, name, email, password } = await request.json();

    if (action === 'register') {
      // 检查用户是否已存在
      const existingUser = users.find(user => user.email === email);
      if (existingUser) {
        return new Response(
          JSON.stringify({ error: '用户已存在' }),
          { status: 400, headers: { 'Content-Type': 'application/json' } }
        );
      }

      // 注册新用户（实际项目中需要加密密码）
      const newUser = {
        id: Date.now().toString(),
        name,
        email,
        password // 注意：实际项目中需要加密密码
      };
      users.push(newUser);
      
      return new Response(
        JSON.stringify({ 
          message: '注册成功',
          user: { id: newUser.id, name: newUser.name, email: newUser.email }
        }),
        { status: 201, headers: { 'Content-Type': 'application/json' } }
      );
    }

    if (action === 'login') {
      // 验证用户凭据
      const user = users.find(user => user.email === email && user.password === password);
      if (!user) {
        return new Response(
          JSON.stringify({ error: '邮箱或密码错误' }),
          { status: 401, headers: { 'Content-Type': 'application/json' } }
        );
      }

      // 生成JWT token（实际项目中需要使用真正的JWT库）
      const token = `fake-jwt-token-${Date.now()}-${email}`;
      
      return new Response(
        JSON.stringify({ 
          message: '登录成功',
          token,
          user: { id: user.id, name: user.name, email: user.email }
        }),
        { status: 200, headers: { 'Content-Type': 'application/json' } }
      );
    }

    return new Response(
      JSON.stringify({ error: '无效的操作' }),
      { status: 400, headers: { 'Content-Type': 'application/json' } }
    );
  } catch (error) {
    console.error('认证错误:', error);
    return new Response(
      JSON.stringify({ error: '服务器内部错误' }),
      { status: 500, headers: { 'Content-Type': 'application/json' } }
    );
  }
}