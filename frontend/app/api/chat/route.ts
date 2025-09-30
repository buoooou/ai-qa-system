import { NextResponse } from 'next/server';

const BACKEND_API_URL = process.env.BACKEND_QA_API_URL || 'http://localhost:8080';

// 创建新会话
export async function POST(req: Request) {
  const { userid } = await req.json();
  try {
    const response = await fetch(`${BACKEND_API_URL}/api/qa/sessions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userid }),
    });
    const data = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    return NextResponse.json(
      { error: 'Failed to create session' },
      { status: 500 }
    );
  }
}

// 删除会话历史
export async function DELETE(req: Request) {
  const { sessionId } = await req.json();
  try {
    const response = await fetch(`${BACKEND_API_URL}/api/qa/sessions`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sessionId }),
    });

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
      content: data,
    });

  } catch (error) {
    return NextResponse.json(
      { error: 'Failed to delete session' },
      { status: 500 }
    );
  }
}