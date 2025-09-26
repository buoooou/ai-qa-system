import { NextResponse } from 'next/server';

// 发送消息并获取 AI 回复
export async function POST(req: Request) {
  const { sessionId, userId, question } = await req.json();
  try {
    console.log('Sending request to QA service:', { sessionId, userId, question });
    const response = await fetch('http://172.18.0.5:8082/api/qa/messages', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sessionId, userId, question }),
    });

    console.log('QA service response status:', response.status);
    console.log('QA service response headers:', Object.fromEntries(response.headers.entries()));

    // 兼容纯文本和 JSON 响应
    const contentType = response.headers.get('Content-Type') || '';
    let data;
    if (contentType.includes('application/json')) {
      data = await response.json();
    } else {
      data = await response.text();
    }

    console.log('QA service response data:', data);

    return NextResponse.json({
      status: response.status,
      content: data,
    });
  } catch (error) {
    console.error('Error calling QA service:', error);
    return NextResponse.json(
      { error: 'Failed to send message', details: error instanceof Error ? error.message : 'Unknown error' },
      { status: 500 }
    );
  }
}