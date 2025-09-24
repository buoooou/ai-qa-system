import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { AuthUtils } from '@/utils/auth';
import { qaApi } from '@/utils/api';
import { ChatMessage, QuestionRequest, User } from '@/types';

export default function Home() {
  const router = useRouter();
  const [user, setUser] = useState<User | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [sessionId] = useState(`session_${Date.now()}`);

  useEffect(() => {
    if (!AuthUtils.isAuthenticated()) {
      router.push('/login');
      return;
    }

    const currentUser = AuthUtils.getUser();
    if (currentUser) {
      setUser(currentUser);
      setMessages([
        {
          id: 'welcome',
          type: 'bot',
          content: `欢迎回来，${currentUser.nickname || currentUser.userName}！有什么问题我可以帮助您？`,
          timestamp: new Date()
        }
      ]);
    }
  }, [router]);

  const handleSendMessage = async () => {
    if (!inputValue.trim() || !user || isLoading) return;

    const userMessage: ChatMessage = {
      id: Date.now().toString(),
      type: 'user',
      content: inputValue,
      timestamp: new Date()
    };

    setMessages(prev => [...prev, userMessage]);
    setInputValue('');
    setIsLoading(true);

    try {
      const request: QuestionRequest = {
        question: inputValue,
        userId: user.id,
        sessionId: sessionId,
        questionType: 'general'
      };

      const response = await qaApi.askQuestion(request);

      if (response.code === 200) {
        const botMessage: ChatMessage = {
          id: response.data.id.toString(),
          type: 'bot',
          content: response.data.answer,
          timestamp: new Date()
        };
        setMessages(prev => [...prev, botMessage]);
      } else {
        throw new Error(response.message);
      }
    } catch (error) {
      const errorMessage: ChatMessage = {
        id: Date.now().toString(),
        type: 'bot',
        content: '抱歉，出现了错误，请稍后再试。',
        timestamp: new Date()
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  const handleLogout = () => {
    AuthUtils.logout();
  };

  if (!user) return null;

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-4">
            <h1 className="text-2xl font-bold text-gray-900">AI Q&A System</h1>
            <div className="flex items-center space-x-4">
              <span className="text-gray-600">Hello, {user.nickname || user.userName}</span>
              <button
                onClick={() => router.push('/history')}
                className="text-blue-600 hover:text-blue-800"
              >
                History
              </button>
              <button
                onClick={handleLogout}
                className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Chat Area */}
      <div className="max-w-4xl mx-auto p-4">
        <div className="bg-white rounded-lg shadow chat-container overflow-y-auto p-6 mb-4">
          {messages.map((message) => (
            <div
              key={message.id}
              className={`flex mb-4 ${message.type === 'user' ? 'justify-end' : 'justify-start'}`}
            >
              <div
                className={`message-bubble p-3 rounded-lg ${
                  message.type === 'user' ? 'user-message' : 'bot-message'
                }`}
              >
                <p className="text-sm">{message.content}</p>
                <p className="text-xs mt-1 opacity-70">
                  {message.timestamp.toLocaleTimeString()}
                </p>
              </div>
            </div>
          ))}
          {isLoading && (
            <div className="flex justify-start mb-4">
              <div className="message-bubble bot-message p-3 rounded-lg">
                <p className="text-sm">正在思考中...</p>
              </div>
            </div>
          )}
        </div>

        {/* Input Area */}
        <div className="bg-white rounded-lg shadow p-4">
          <div className="flex space-x-4">
            <textarea
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              onKeyPress={handleKeyPress}
              placeholder="输入您的问题..."
              className="flex-1 border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
              rows={3}
              disabled={isLoading}
            />
            <button
              onClick={handleSendMessage}
              disabled={!inputValue.trim() || isLoading}
              className="bg-blue-500 text-white px-6 py-2 rounded-lg hover:bg-blue-600 disabled:bg-gray-400 disabled:cursor-not-allowed"
            >
              发送
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}