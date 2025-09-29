import { NextResponse } from 'next/server';

const BACKEND_API_URL = process.env.BACKEND_QA_API_URL || 'http://localhost:8080';

// 用户退出删除缓存会话ID
export async function DELETE(req: Request) {
  const { sessionId } = await req.json();
  console.log('Logout request:', { sessionId, backendUrl: BACKEND_API_URL });
  try {
    const response = await fetch(`${BACKEND_API_URL}/api/qa/sessions/logout`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sessionId }),
    });
    console.log('Backend response:', response.status, response.statusText);
    
    // 兼容纯文本和 JSON 响应
    const contentType = response.headers.get('Content-Type') || '';
    let data;
    if (contentType.includes('application/json')) {
      data = await response.json();
    } else {
      data = await response.text();
    }
    
    return NextResponse.json({
      status: response.status,
      message: data,
    });
  } catch (error) {
    console.error('Logout error:', error);
    return NextResponse.json(
      { error: 'Failed to delete session' },
      { status: 500 }
    );
  }
}
