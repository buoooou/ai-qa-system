import { NextRequest } from 'next/server';

export async function POST(request: NextRequest) {
  try {
    const { messages } = await request.json();
    
    // 获取用户最新的问题
    const lastMessage = messages[messages.length - 1];
    
    if (!lastMessage || lastMessage.role !== 'user') {
      return new Response(
        JSON.stringify({ error: '无效的消息格式' }),
        { status: 400, headers: { 'Content-Type': 'application/json' } }
      );
    }

    // 模拟与AI模型的交互
    // 实际项目中这里会调用Google AI或其他AI服务
    const aiResponse = `这是对"${lastMessage.content}"的模拟回复。在实际应用中，这里会调用AI模型API来生成智能回复。`;

    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 1000));

    return new Response(
      JSON.stringify({ 
        role: 'assistant',
        content: aiResponse
      }),
      { 
        status: 200, 
        headers: { 
          'Content-Type': 'application/json',
        }
      }
    );
  } catch (error) {
    console.error('AI聊天错误:', error);
    return new Response(
      JSON.stringify({ error: '服务器内部错误' }),
      { status: 500, headers: { 'Content-Type': 'application/json' } }
    );
  }
}