'use client';
import { useState, useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';

interface ChatMessage {
  id: string;
  role: 'user' | 'assistant';
  content: string;
}

interface HistorySession {
  id: string;
  title: string;
}

export default function ChatPage() {
  const router = useRouter();
  const [messages, setMessages] = useState<ChatMessage[]>([
    { id: '1', role: 'assistant', content: '你好！请随意输入消息。' },
  ]);
  const [input, setInput] = useState('');
  const [historySessions, setHistorySessions] = useState<HistorySession[]>([]);
  const [theme, setTheme] = useState<'dark' | 'light' | 'blue' | 'green'>('dark');
  const [nickname, setNickname] = useState<string>(
    typeof window !== 'undefined' ? localStorage.getItem('nickname') || '用户' : '用户'
  );
  const [isEditingNickname, setIsEditingNickname] = useState(false);
  const [nicknameInput, setNicknameInput] = useState(nickname);
  const [isThinking, setIsThinking] = useState(false);

  const handleLogout = async () => {
    try{
      const response = await fetch('/api/auth/logout', {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sessionId: localStorage.getItem('sessionId') }),
      });
      console.log("logout", response);
      const data = await response.json();
      console.log("data", data);
      if (data.status === 200) {
        toast.success(data.message);
      } else {
        toast.error(`${data.message},2秒后将被强制退出` || '2秒后将被强制退出');
      }
      await new Promise(resolve => setTimeout(resolve, 2000));
      localStorage.clear();
      router.push('/login');
    } catch (error) {
      toast.error('网络错误，请重试');
    } 
  }

  const handleNicknameSubmit = async () => {
    if (nicknameInput.trim() === nickname) {
      setIsEditingNickname(false);
      return;
    }

    try {
      const response = await fetch('/api/users/update', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nickname: nicknameInput, username: localStorage.getItem('username') }),
      });
      const data = await response.json();
      if (data.code === 200) {
        localStorage.setItem('nickname', nicknameInput);
        setNickname(nicknameInput);
        toast.success('昵称更新成功');
      } else {
        toast.error(data.message || '昵称更新失败');
      }
    } catch (error) {
      toast.error('网络错误，请重试');
    } finally {
      setIsEditingNickname(false);
    }
  };

  const initialized = useRef(false);

  useEffect(() => {
    if (typeof window !== 'undefined' && !initialized.current) {
      const handleStorageChange = () => {
        setNickname(localStorage.getItem('nickname') || '用户');
      };
      window.addEventListener('storage', handleStorageChange);
      handleInitSession();
      initialized.current = true;
      return () => window.removeEventListener('storage', handleStorageChange);
    }
  }, []);

  const toggleTheme = () => {
    const themes: ('dark' | 'light' | 'blue' | 'green')[] = ['dark', 'light', 'blue', 'green'];
    const currentIndex = themes.indexOf(theme);
    const nextIndex = (currentIndex + 1) % themes.length;
    setTheme(themes[nextIndex]);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    console.log('handleSubmit called');
    if (!input.trim()) return;

    // 用户发送消息
    const userMessage: ChatMessage = {
      id: Date.now().toString(),
      role: 'user',
      content: input,
    };
    setMessages([...messages, userMessage]);
    setInput('');
    setIsThinking(true); // 显示“思考中...”动画

    try {
      // 调用发送消息接口
      const response = await fetch('/api/chat/send-message', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          sessionId: localStorage.getItem('sessionId'),
          userId: localStorage.getItem('userid'),
          question: input,
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        const errorMessage = errorData.error || errorData.message || '发送消息失败';
        setIsThinking(false);
        toast.error(errorMessage);
        const errorMessageObj: ChatMessage = {
          id: Date.now().toString(),
          role: 'assistant',
          content: '系统繁忙，请稍后再试',
        };
        setMessages((prevMessages) => [...prevMessages, errorMessageObj]);
        return;
      }

      // 获取完整响应内容
      const data = await response.json();
      const fullResponse = data.content || data.message || 'AI回复内容';

      // 创建AI回复的初始消息
      const aiMessageId = Date.now().toString();
      const aiMessage: ChatMessage = {
        id: aiMessageId,
        role: 'assistant',
        content: '',
      };
      setMessages((prevMessages) => [...prevMessages, aiMessage]);

      // 模拟流式输出
      let chunkIndex = 0;
      const chunkSize = 5; // 每次显示的字符数
      const interval = setInterval(() => {
        if (chunkIndex >= fullResponse.length) {
          clearInterval(interval);
          setIsThinking(false);
          return;
        }

        const chunk = fullResponse.substring(chunkIndex, chunkIndex + chunkSize);
        chunkIndex += chunkSize;

        // 更新AI回复内容
        setMessages((prevMessages) =>
          prevMessages.map((msg) =>
            msg.id === aiMessageId ? { ...msg, content: msg.content + chunk } : msg
          )
        );
      }, 100); // 每100毫秒更新一次
    } catch (error) {
      toast.error('网络错误，请重试');
      // 添加网络错误提示到消息列表
      const errorMessageObj: ChatMessage = {
        id: Date.now().toString(),
        role: 'assistant',
        content: '网络异常，请检查连接后重试',
      };
      setMessages((prevMessages) => [...prevMessages, errorMessageObj]);
      setIsThinking(false); // 隐藏“思考中...”动画
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInput(e.target.value);
  };

  const handleNewSession = async () => {
    try {
      const response = await fetch('/api/chat/new-session', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          sessionId: localStorage.getItem('sessionId'),
          userid: localStorage.getItem('userid'),
          topic: messages.length > 1 ? messages[1].content : ''
        }),
      });
      const data = await response.json();
      console.log("history session Data:", data)
      if (response.ok) {
        setMessages([{ id: '1', role: 'assistant', content: '我是AI助手，你有什么问题？' }]);
        toast.success('新会话创建成功');
        localStorage.setItem('sessionId', data.sessionId);
        setHistorySessions(prevSessions => {
          const newSession = { id: data.sessionId, title: messages.length > 1 ? messages[1].content : '新会话' };
          console.log("Adding new session:", newSession);
          return [...prevSessions, newSession];
        });
      } else {
        toast.error(data.error || '创建会话失败');
      }
    } catch (error) {
      toast.error('网络错误，请重试');
    }
  };

  const handleInitSession = async () => {
    try {
      const response = await fetch('/api/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
          userid: localStorage.getItem('userid'),
        }),
      });
      const data = await response.json();
      console.log("新建会话", data);
      if (response.ok) {
        setMessages([{ id: '1', role: 'assistant', content: '我是AI助手，你有什么问题？' }]);
        toast.success('新会话创建成功');
        localStorage.setItem('sessionId', data.sessionId);
        // 解析 historySessionId 和 historySessionTopic
        const { historySessionId = [], historySessionTopic = [] } = data;
        if (historySessionId.length > 0 && historySessionTopic.length > 0 && historySessionId.length === historySessionTopic.length) {
          const newHistorySessions = historySessionId.map((id: string, index: number) => ({
            id,
            title: historySessionTopic[index],
          }));
          setHistorySessions(newHistorySessions);
        }
      } else {
        toast.error(data.message || '新会话创建失败');
      }
    } catch (error) {
      toast.error('网络错误，请重试');
    }
  };

  const handleLoadHistory = async (sid: string) => {
    // 加载历史会话
    console.log("handleLoadHistory:", sid);
    try {
      const response = await fetch('/api/chat/qa-history', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          sessionId: localStorage.getItem('sessionId'),
          userid: localStorage.getItem('userid'),
          topic: messages.length > 1 ? messages[1].content : '',
          specifySId: sid
        }),
      });
      const data = await response.json();
      console.log("after load Data:", data)
      if (response.ok) {
        toast.success('会话加载成功');
        const formattedMessages: ChatMessage[] = [];
        data.question.forEach((q: string, index: number) => {
          formattedMessages.push({
            id: `q-${index + 1}`,
            role: 'user',
            content: q,
          });
          if (data.answer[index]) {
            formattedMessages.push({
              id: `a-${index + 1}`,
              role: 'assistant',
              content: data.answer[index],
            });
          }
        });
        setMessages(formattedMessages);
      } else {
        toast.error(data.error || '加载会话失败');
      }
    } catch (error) {
      toast.error('网络错误，请重试');
      console.log(error)
    }
  };

  const handleDeleteQAHistory = async (sid: string) => {
    console.log("handleDeleteQAHistory:", sid);
    try {
      const response = await fetch('/api/chat', {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          sessionId: sid
        }),
      });
      const data = await response.json();
      console.log("after del Data:", data)
      if (response.ok) {
        toast.success('会话删除成功');
        setHistorySessions(prevSessions => {
          console.log("Before deletion:", prevSessions);
          const updatedSessions = prevSessions.filter(session => session.id !== sid);
          console.log("After deletion:", updatedSessions);
          return updatedSessions;
        });
      } else {
        toast.error(data.error || '删除会话失败');
      }
    } catch (error) {
      toast.error('网络错误，请重试');
    }
  };

  return (
      <div className={`flex h-screen overflow-visible ${theme === 'dark' ? 'bg-gray-900 text-teal-50' : theme === 'light' ? 'bg-amber-50 text-gray-800' : theme === 'blue' ? 'bg-slate-900 text-sky-100' : 'bg-emerald-950 text-emerald-50'}`}>
      {/* 侧边栏 */}
      <div className={`w-64 p-4 border-r flex flex-col h-full ${theme === 'dark' ? 'bg-gray-800 border-teal-700/30' : theme === 'light' ? 'bg-amber-100 border-amber-300' : theme === 'blue' ? 'bg-slate-800 border-sky-700/30' : 'bg-emerald-900 border-emerald-700/30'}`}>

        <button
          onClick={handleNewSession}
          className={`w-full p-3 mb-4 rounded-lg hover:opacity-90 transition-colors ${theme === 'dark' ? 'bg-teal-600 text-white hover:bg-teal-700' : theme === 'light' ? 'bg-blue-500 text-white hover:bg-blue-600' : theme === 'blue' ? 'bg-sky-600 text-white hover:bg-sky-700' : 'bg-emerald-600 text-white hover:bg-emerald-700'}`}
        >
          新加会话
        </button>
        <div className={`mb-2 font-semibold ${theme === 'dark' ? 'text-teal-300/80' : theme === 'light' ? 'text-blue-600/80' : theme === 'blue' ? 'text-sky-300/80' : 'text-emerald-300/80'}`}>历史会话</div>
        <div className="space-y-2 flex-grow whitespace-nowrap truncate">
          {historySessions.length > 0 ? (
            historySessions.map((session) => (
              <div
                key={session.id}
                className={`p-3 rounded-lg cursor-pointer transition-colors ${theme === 'dark' ? 'bg-gray-700/60 hover:bg-gray-600/80 text-teal-100' : theme === 'light' ? 'bg-amber-200 hover:bg-amber-300 text-blue-800' : theme === 'blue' ? 'bg-slate-700/60 hover:bg-slate-600/80 text-sky-100' : 'bg-emerald-800/60 hover:bg-emerald-700/80 text-emerald-100'}`}
                onClick={() => handleLoadHistory(session.id)}
              >
                <div className="flex justify-between items-center">
                  <span>{session.title}</span>
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      if (confirm('是否删除历史会话？')) {
                        handleDeleteQAHistory(session.id);
                      }
                    }}
                    className={`w-6 h-6 rounded-full flex items-center justify-center ${theme === 'dark' ? 'bg-gray-600 hover:bg-gray-500 text-teal-100' : theme === 'light' ? 'bg-amber-300 hover:bg-amber-400 text-blue-800' : theme === 'blue' ? 'bg-slate-600 hover:bg-slate-500 text-sky-100' : 'bg-emerald-700 hover:bg-emerald-600 text-emerald-100'}`}
                  >
                    ×
                  </button>
                </div>
              </div>
            ))
          ) : (
            <div className="p-3 text-gray-500">暂无历史会话</div>
          )}
        </div>
        {/* 用户昵称显示区域 */}
        <div 
          className={`p-4 rounded-lg ${theme === 'dark' ? 'bg-gray-800/60 border-t border-teal-700/30' : theme === 'light' ? 'bg-amber-100 border-t border-amber-300' : theme === 'blue' ? 'bg-slate-800/60 border-t border-sky-700/30' : 'bg-emerald-900/60 border-t border-emerald-700/30'}`}
          onClick={(e) => {
            e.stopPropagation();
            const menus = document.querySelectorAll('.user-menu');
            menus.forEach(menu => menu.classList.add('hidden'));
          }}
        >
          <div className="flex items-center justify-between mb-4 spaace-y-2 whitespace-nowrap p-2 rounded-full mx-auto">
            {isEditingNickname ? (
              <input
                type="text"
                value={nicknameInput}
                onChange={(e) => setNicknameInput(e.target.value)}
                onBlur={handleNicknameSubmit}
                onKeyDown={(e) => e.key === 'Enter' && handleNicknameSubmit()}
                className={`font-semibold w-full ${theme === 'dark' ? 'text-teal-300 bg-gray-700' : theme === 'light' ? 'text-blue-600 bg-amber-100' : theme === 'blue' ? 'text-sky-300 bg-slate-700' : 'text-emerald-300 bg-emerald-800'} p-2 rounded border-none outline-none`}
                autoFocus
              />
            ) : (
              <div 
                className={`font-semibold ${theme === 'dark' ? 'text-teal-300' : theme === 'light' ? 'text-blue-600' : theme === 'blue' ? 'text-sky-300' : 'text-emerald-300'} p-2 rounded`}
              >
                {nickname}
              </div>
            )}
            <div className="relative">
              <button 
                className={`p-1 rounded-full w-8 h-8 flex items-center justify-center cursor-pointer ${theme === 'dark' ? 'bg-gray-700 text-teal-300 hover:bg-gray-600' : theme === 'light' ? 'bg-amber-200 text-blue-600 hover:bg-amber-300' : theme === 'blue' ? 'bg-slate-700 text-sky-300 hover:bg-slate-600' : 'bg-emerald-800 text-emerald-300 hover:bg-emerald-700'}`}
                onClick={(e) => {
                  e.stopPropagation();
                  const menu = e.currentTarget.nextElementSibling as HTMLElement;
                  menu.classList.toggle('hidden');
                }}
              >
                ...
              </button>
              <div className={`absolute right-0 bottom-full mb-2 w-48 rounded-md shadow-lg ${theme === 'dark' ? 'bg-gray-700' : theme === 'light' ? 'bg-amber-100' : theme === 'blue' ? 'bg-slate-700' : 'bg-emerald-800'} z-50 hidden user-menu`}>
                <div className="py-1">
                  <button 
                    className={`block w-full text-left px-4 py-2 ${theme === 'dark' ? 'text-teal-300 hover:bg-gray-600' : theme === 'light' ? 'text-blue-600 hover:bg-amber-200' : theme === 'blue' ? 'text-sky-300 hover:bg-slate-600' : 'text-emerald-300 hover:bg-emerald-700'}`}
                    onClick={() => {
                      setIsEditingNickname(true);
                    }}
                  >
                    更换昵称
                  </button>
                  <button 
                    className={`block w-full text-left px-4 py-2 ${theme === 'dark' ? 'text-teal-300 hover:bg-gray-600' : theme === 'light' ? 'text-blue-600 hover:bg-amber-200' : theme === 'blue' ? 'text-sky-300 hover:bg-slate-600' : 'text-emerald-300 hover:bg-emerald-700'}`}
                    onClick={() =>handleLogout()}
                  >
                    退出
                  </button>
                </div>
              </div>
            </div>
          </div>
          {/* 侧边栏底部主题切换按钮 */}
          <div className={`p-2 rounded-full w-fit mx-auto ${theme === 'dark' ? 'bg-gray-700 text-teal-300' : theme === 'light' ? 'bg-amber-200 text-blue-600' : theme === 'blue' ? 'bg-slate-700 text-sky-300' : 'bg-emerald-800 text-emerald-300'} cursor-pointer`} onClick={(e) => {
            e.stopPropagation();
            const themes = ['dark', 'light', 'blue', 'green'];
            const nextTheme = themes[(themes.indexOf(theme) + 1) % themes.length] as 'dark' | 'light' | 'blue' | 'green';
            setTheme(nextTheme);
            toast.success(`已切换至${nextTheme === 'dark' ? '深色' : nextTheme === 'light' ? '浅色' : nextTheme === 'blue' ? '蓝色' : '绿色'}主题`);
          }}>
            选择你喜欢的主题
            {theme === 'dark' ? '🌙' : theme === 'light' ? '☀️' : theme === 'blue' ? '🔵' : '🟢'}
          </div>
        </div>
      </div>

      {/* 主聊天区域 */}
      <div className="flex-1 flex flex-col max-w-3xl p-4 mx-auto">
        <div className="flex-grow overflow-y-auto mb-4 space-y-4">
          {messages.map((message: ChatMessage) => (
            <div
              key={message.id}
              className={`p-4 rounded-lg ${message.role === 'user' ? (theme === 'dark' ? 'bg-teal-800/80' : theme === 'light' ? 'bg-blue-500/90' : theme === 'blue' ? 'bg-sky-800/80' : 'bg-emerald-800/80') : (theme === 'dark' ? 'bg-gray-700/50' : theme === 'light' ? 'bg-amber-100' : theme === 'blue' ? 'bg-slate-700/50' : 'bg-emerald-900/50')}`}
            >
              <span className={`font-bold ${message.role === 'user' ? (theme === 'dark' ? 'text-teal-300' : theme === 'light' ? 'text-blue-100' : theme === 'blue' ? 'text-sky-300' : 'text-emerald-300') : (theme === 'dark' ? 'text-teal-200' : theme === 'light' ? 'text-blue-800' : theme === 'blue' ? 'text-sky-200' : 'text-emerald-200')}`}>
                {message.role === 'user' ? 'You: ' : 'AI: '}
              </span>
              <span className={theme === 'dark' ? 'text-teal-50' : theme === 'light' ? 'text-gray-800' : theme === 'blue' ? 'text-sky-100' : 'text-emerald-50'}>{message.content}</span>
            </div>
          ))}
        </div>

        <form onSubmit={handleSubmit} className="flex gap-2">
          <input
            type="text"
            value={input}
            onChange={handleInputChange}
            placeholder="输入消息..."
            className={`flex-grow p-3 border rounded-lg focus:outline-none focus:ring-2 ${theme === 'dark' ? 'bg-gray-700/50 border-teal-700/50 focus:ring-teal-400' : theme === 'light' ? 'bg-white border-blue-400/50 focus:ring-blue-300' : theme === 'blue' ? 'bg-slate-700/50 border-sky-700/50 focus:ring-sky-400' : 'bg-emerald-800/50 border-emerald-700/50 focus:ring-emerald-400'}`}
          />
          <button
            type="submit"
            className={`p-3 text-white rounded-lg hover:opacity-90 transition-colors ${theme === 'dark' ? 'bg-teal-600 hover:bg-teal-700' : theme === 'light' ? 'bg-blue-500 hover:bg-blue-600' : theme === 'blue' ? 'bg-sky-600 hover:bg-sky-700' : 'bg-emerald-600 hover:bg-emerald-700'}`}
          >
            发送
          </button>
        </form>
      </div>
    </div>
  );
}