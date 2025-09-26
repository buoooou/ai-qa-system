import { NextResponse } from 'next/server';

// 创建新会话
export async function POST(req: Request) {
  const { sessionId, userid, topic } = await req.json();

  try {
    const response = await fetch('http://qa-service:8082/api/qa/sessions/new', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sessionId, userid, topic }),
    });

    const data = await response.json();
    return NextResponse.json(data);
  } catch {
    return NextResponse.json(
      { error: 'Failed to create new session' },
      { status: 500 }
    );
  }
}