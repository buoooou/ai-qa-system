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

    // 调用智谱清言API
    const apiKey = "11573aa296234faca13fc82a7cc55403.PfKqfGqI7YY3fyHZ";
    const model = "glm-4-plus"; // 使用GLM-4-Plus模型
    
    const response = await fetch('https://open.bigmodel.cn/api/paas/v4/chat/completions', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${apiKey}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        model: model,
        messages: messages,
        stream: false, // 同步调用
        temperature: 0.75, // 控制输出随机性
        top_p: 0.9, // 核采样参数
        max_tokens: 2000, // 最大输出token数
      }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error('智谱清言API错误响应:', errorText);
      throw new Error(`智谱清言API请求失败，状态码: ${response.status}`);
    }

    const data = await response.json();
    
    // 检查响应格式
    if (!data.choices || !data.choices[0] || !data.choices[0].message) {
      console.error('智谱清言API响应格式异常:', data);
      throw new Error('API响应格式异常');
    }
    
    const aiResponse = data.choices[0].message.content;

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