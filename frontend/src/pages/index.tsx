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

    const currentUser = AuthUtils?.getUser();
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
    <div className="min-h-screen">
      {/* Header */}
      <header className="header-blur sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-6">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-gradient-to-r from-purple-400 to-pink-400 rounded-full flex items-center justify-center">
                <span className="text-white font-bold text-lg">AI</span>
              </div>
              <h1 className="text-2xl font-bold text-white">AI Q&A System</h1>
            </div>
            <div className="flex items-center space-x-6">
              <span className="text-white/90 font-medium">你好, {user.nickname || user.userName}</span>
              <button
                onClick={() => router.push('/history')}
                className="text-white/90 hover:text-white transition-colors duration-200 font-medium flex items-center space-x-1"
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <span>历史记录</span>
              </button>
              <button
                onClick={handleLogout}
                className="bg-red-500/80 backdrop-blur text-white px-4 py-2 rounded-full hover:bg-red-600/90 transition-all duration-200 font-medium flex items-center space-x-1"
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                </svg>
                <span>退出</span>
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Chat Area */}
      <div className="max-w-5xl mx-auto p-6">
        <div className="chat-container overflow-y-auto p-8 mb-6 fade-in">
          <div className="space-y-6">
            {messages.map((message) => (
              <div
                key={message.id}
                className={`flex ${message.type === 'user' ? 'justify-end' : 'justify-start'} fade-in`}
              >
                <div className="flex items-start space-x-3 max-w-3xl">
                  {message.type === 'bot' && (
                    <div className="flex-shrink-0 w-8 h-8 bg-gradient-to-r from-blue-500 to-purple-600 rounded-full flex items-center justify-center">
                      <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
                      </svg>
                    </div>
                  )}
                  <div
                    className={`message-bubble p-4 ${
                      message.type === 'user' ? 'user-message' : 'bot-message'
                    }`}
                  >
                    <p className="text-sm leading-relaxed whitespace-pre-wrap">{message.content}</p>
                    <p className="text-xs mt-2 opacity-70 font-medium">
                      {message.timestamp.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}
                    </p>
                  </div>
                  {message.type === 'user' && (
                    <div className="flex-shrink-0 w-8 h-8 bg-gradient-to-r from-pink-500 to-rose-500 rounded-full flex items-center justify-center">
                      <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                      </svg>
                    </div>
                  )}
                </div>
              </div>
            ))}
            {isLoading && (
              <div className="flex justify-start fade-in">
                <div className="flex items-start space-x-3">
                  <div className="flex-shrink-0 w-8 h-8 bg-gradient-to-r from-blue-500 to-purple-600 rounded-full flex items-center justify-center">
                    <div className="loading-animation"></div>
                  </div>
                  <div className="message-bubble bot-message p-4">
                    <p className="text-sm flex items-center space-x-2">
                      <span>AI正在思考中</span>
                      <div className="flex space-x-1">
                        <div className="w-2 h-2 bg-blue-500 rounded-full animate-bounce"></div>
                        <div className="w-2 h-2 bg-blue-500 rounded-full animate-bounce" style={{animationDelay: '0.1s'}}></div>
                        <div className="w-2 h-2 bg-blue-500 rounded-full animate-bounce" style={{animationDelay: '0.2s'}}></div>
                      </div>
                    </p>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Input Area */}
        <div className="input-container p-6 fade-in">
          <div className="flex items-end space-x-4">
            <div className="flex-1 relative">
              <textarea
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="在这里输入您的问题... (按Enter发送，Shift+Enter换行)"
                className="w-full border-0 bg-white/50 backdrop-blur rounded-xl px-6 py-4 focus:ring-2 focus:ring-purple-500 focus:outline-none resize-none placeholder-gray-500 text-gray-700 shadow-inner"
                rows={3}
                disabled={isLoading}
              />
            </div>
            <button
              onClick={handleSendMessage}
              disabled={!inputValue.trim() || isLoading}
              className="gradient-button text-white px-8 py-4 rounded-xl font-medium disabled:cursor-not-allowed flex items-center space-x-2"
            >
              {isLoading ? (
                <>
                  <div className="loading-animation"></div>
                  <span>发送中</span>
                </>
              ) : (
                <>
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
                  </svg>
                  <span>发送</span>
                </>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}