'use client';

import React, { useState, useRef, useEffect } from 'react';
import { qaApi, QaRequest, QaResponse, User } from '@/services/api';

interface ChatInterfaceProps {
  user: User;
  onLogout: () => void;
}

interface Message {
  id: string;
  type: 'user' | 'ai';
  content: string;
  timestamp: Date;
  loading?: boolean;
}

const ChatInterface: React.FC<ChatInterfaceProps> = ({ user, onLogout }) => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = useState(false);
  const [history, setHistory] = useState<QaResponse[]>([]);
  const [showHistory, setShowHistory] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLTextAreaElement>(null);

  // 自动滚动到底部
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // 加载历史记录
  const loadHistory = async () => {
    try {
      const response = await qaApi.getUserHistoryPaged(user.id, 0, 20);
      if (response.success && response.data) {
        setHistory(response.data);
      }
    } catch (error) {
      console.error('Failed to load history:', error);
    }
  };

  useEffect(() => {
    loadHistory();
    // 添加欢迎消息
    setMessages([
      {
        id: 'welcome',
        type: 'ai',
        content: `你好，${user.userName}！我是AI智能助手，很高兴为您服务。请问有什么可以帮助您的吗？`,
        timestamp: new Date(),
      },
    ]);
  }, [user]);

  // 发送消息
  const handleSendMessage = async () => {
    if (!inputValue.trim() || loading) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      type: 'user',
      content: inputValue.trim(),
      timestamp: new Date(),
    };

    const loadingMessage: Message = {
      id: Date.now().toString() + '_loading',
      type: 'ai',
      content: '正在思考中...',
      timestamp: new Date(),
      loading: true,
    };

    setMessages(prev => [...prev, userMessage, loadingMessage]);
    setInputValue('');
    setLoading(true);

    try {
      const qaRequest: QaRequest = {
        userId: user.id,
        question: userMessage.content,
        includeHistory: true,
      };

      const response = await qaApi.askQuestion(qaRequest);

      if (response.success && response.data) {
        const aiMessage: Message = {
          id: response.data.id.toString(),
          type: 'ai',
          content: response.data.answer,
          timestamp: new Date(response.data.createTime),
        };

        // 移除loading消息，添加AI回复
        setMessages(prev => prev.filter(msg => !msg.loading).concat(aiMessage));

        // 刷新历史记录
        loadHistory();
      } else {
        throw new Error(response.message || '获取回答失败');
      }
    } catch (error: any) {
      console.error('Failed to get AI response:', error);

      const errorMessage: Message = {
        id: Date.now().toString() + '_error',
        type: 'ai',
        content: `抱歉，我遇到了一些问题：${error.response?.data?.message || error.message || '请稍后重试'}`,
        timestamp: new Date(),
      };

      // 移除loading消息，添加错误消息
      setMessages(prev => prev.filter(msg => !msg.loading).concat(errorMessage));
    } finally {
      setLoading(false);
      inputRef.current?.focus();
    }
  };

  // 处理键盘事件
  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  // 清空对话
  const handleClearChat = () => {
    setMessages([
      {
        id: 'welcome_new',
        type: 'ai',
        content: `对话已清空。${user.userName}，有什么新问题需要我帮助吗？`,
        timestamp: new Date(),
      },
    ]);
  };

  // 开始新对话
  const handleNewChat = () => {
    setMessages([
      {
        id: 'new_chat_' + Date.now(),
        type: 'ai',
        content: `${user.userName}，我们开始一个全新的对话吧！有什么问题需要我帮助您解决吗？`,
        timestamp: new Date(),
      },
    ]);
  };

  // 从历史记录加载问题
  const handleLoadFromHistory = (qa: QaResponse) => {
    setInputValue(qa.question);
    setShowHistory(false);
    inputRef.current?.focus();
  };

  return (
    <div className="flex h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50">
      {/* 侧边栏 */}
      <div className="w-80 bg-white/80 backdrop-blur-sm shadow-xl border-r border-white/20 flex flex-col rounded-r-3xl">
        {/* 用户信息区域 */}
        <div className="px-6 py-4 bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600 text-white rounded-tr-3xl">
          <div className="flex items-center space-x-4 h-16">
            <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center ring-2 ring-white/30">
              <span className="text-lg font-bold">{user.userName.charAt(0).toUpperCase()}</span>
            </div>
            <div className="flex-1">
              <h2 className="text-lg font-semibold">{user.userName}</h2>
              <p className="text-blue-100 text-sm opacity-90">{user.email}</p>
            </div>
          </div>
        </div>

        {/* 功能菜单 */}
        <div className="flex-1 p-4">
          <div className="space-y-2">
            <button
              onClick={handleNewChat}
              className="w-full flex items-center space-x-3 p-3 text-gray-700 hover:bg-gradient-to-r hover:from-green-50 hover:to-emerald-50 rounded-xl transition-all duration-200 group"
            >
              <div className="w-8 h-8 bg-gradient-to-r from-green-500 to-emerald-500 rounded-lg flex items-center justify-center group-hover:scale-110 transition-transform duration-200">
                <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                </svg>
              </div>
              <span className="font-medium">开始新对话</span>
            </button>

            <button
              onClick={handleClearChat}
              className="w-full flex items-center space-x-3 p-3 text-gray-700 hover:bg-gradient-to-r hover:from-blue-50 hover:to-indigo-50 rounded-xl transition-all duration-200 group"
            >
              <div className="w-8 h-8 bg-gradient-to-r from-blue-500 to-indigo-500 rounded-lg flex items-center justify-center group-hover:scale-110 transition-transform duration-200">
                <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
              </div>
              <span className="font-medium">清空对话</span>
            </button>

            <button
              onClick={() => setShowHistory(!showHistory)}
              className="w-full flex items-center space-x-3 p-3 text-gray-700 hover:bg-gradient-to-r hover:from-purple-50 hover:to-pink-50 rounded-xl transition-all duration-200 group"
            >
              <div className="w-8 h-8 bg-gradient-to-r from-purple-500 to-pink-500 rounded-lg flex items-center justify-center group-hover:scale-110 transition-transform duration-200">
                <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <span className="font-medium">{showHistory ? '隐藏' : '显示'}历史记录</span>
            </button>
          </div>

          {/* 历史记录 */}
          {showHistory && (
            <div className="mt-4 space-y-2 max-h-96 overflow-y-auto">
              <h4 className="font-semibold text-gray-800 px-2">历史问答</h4>
              {history.map((qa) => (
                <div
                  key={qa.id}
                  onClick={() => handleLoadFromHistory(qa)}
                  className="p-3 bg-gradient-to-r from-gray-50 to-gray-100 hover:from-blue-50 hover:to-indigo-50 rounded-lg cursor-pointer transition-all duration-200 border border-gray-200 hover:border-blue-200 hover:shadow-sm"
                >
                  <p className="text-sm text-gray-800 line-clamp-2 font-medium">
                    {qa.question}
                  </p>
                  <p className="text-xs text-gray-500 mt-1">
                    {new Date(qa.createTime).toLocaleString()}
                  </p>
                </div>
              ))}
              {history.length === 0 && (
                <div className="text-center py-8">
                  <div className="w-16 h-16 bg-gradient-to-r from-gray-200 to-gray-300 rounded-full flex items-center justify-center mx-auto mb-3">
                    <svg className="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                  </div>
                  <p className="text-sm text-gray-500">暂无历史记录</p>
                </div>
              )}
            </div>
          )}
        </div>

        {/* 退出按钮 */}
        <div className="p-4 border-t border-gray-200 mt-auto rounded-br-3xl">
          <button
            onClick={onLogout}
            className="w-full flex items-center justify-center space-x-2 p-3 text-red-600 hover:bg-red-50 rounded-xl transition-all duration-200 group border border-red-200 hover:border-red-300"
          >
            <svg className="w-5 h-5 group-hover:scale-110 transition-transform duration-200" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
            <span className="font-medium">退出登录</span>
          </button>
        </div>
      </div>

      {/* 主聊天区域 */}
      <div className="flex-1 flex flex-col">
        {/* 顶部标题栏 */}
        <div className="bg-white/80 backdrop-blur-sm shadow-sm border-b border-white/20 px-6 py-4 rounded-tl-3xl">
          <div className="flex items-center space-x-4 h-16">
            <div className="w-12 h-12 bg-gradient-to-r from-blue-500 via-indigo-500 to-purple-500 rounded-xl flex items-center justify-center shadow-lg">
              <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
              </svg>
            </div>
            <div>
              <h1 className="text-lg font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
                AI智能问答助手
              </h1>
              <p className="text-gray-600 text-sm">基于微服务架构的智能对话系统</p>
            </div>
          </div>
        </div>

        {/* 消息区域 */}
        <div className="flex-1 overflow-y-auto p-6 space-y-6">
          {messages.map((message) => (
            <div
              key={message.id}
              className={`flex ${message.type === 'user' ? 'justify-end' : 'justify-start'} animate-fade-in`}
            >
              <div className={`flex items-start space-x-3 max-w-3xl ${message.type === 'user' ? 'flex-row-reverse space-x-reverse' : ''}`}>
                {/* 头像 */}
                <div className={`w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0 shadow-lg ${
                  message.type === 'user'
                    ? 'bg-gradient-to-r from-blue-500 to-indigo-500 text-white'
                    : 'bg-gradient-to-r from-emerald-500 to-teal-500 text-white'
                }`}>
                  {message.type === 'user' ? (
                    <span className="text-sm font-bold">{user.userName.charAt(0).toUpperCase()}</span>
                  ) : (
                    <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                    </svg>
                  )}
                </div>

                {/* 消息内容 */}
                <div className={`relative rounded-2xl px-5 py-3 shadow-lg backdrop-blur-sm ${
                  message.type === 'user'
                    ? 'bg-gradient-to-r from-blue-500 to-indigo-500 text-white'
                    : 'bg-gradient-to-r from-green-50 to-emerald-50 border border-green-200 text-gray-800'
                }`}>
                  {/* 小尖角 */}
                  <div className={`absolute top-3 w-0 h-0 ${
                    message.type === 'user'
                      ? 'right-[-8px] border-l-[8px] border-l-blue-500 border-t-[6px] border-t-transparent border-b-[6px] border-b-transparent'
                      : 'left-[-8px] border-r-[8px] border-r-green-100 border-t-[6px] border-t-transparent border-b-[6px] border-b-transparent'
                  }`}></div>
                  {message.loading ? (
                    <div className="flex items-center space-x-3">
                      <div className="flex space-x-1">
                        <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce"></div>
                        <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0.1s' }}></div>
                        <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0.2s' }}></div>
                      </div>
                      <span className="text-sm">{message.content}</span>
                    </div>
                  ) : (
                    <div>
                      <p className="text-sm leading-relaxed whitespace-pre-wrap">{message.content}</p>
                      <p className={`text-xs mt-2 ${
                        message.type === 'user' ? 'text-blue-100' : 'text-gray-500'
                      }`}>
                        {message.timestamp.toLocaleTimeString('zh-CN', {
                          hour: '2-digit',
                          minute: '2-digit'
                        })}
                      </p>
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
          <div ref={messagesEndRef} />
        </div>

        {/* 输入区域 */}
        <div className="bg-white/80 backdrop-blur-sm border-t border-white/20 p-6 rounded-bl-3xl">
          <div className="flex items-center space-x-4 max-w-4xl mx-auto">
            <div className="flex-1 relative">
              <textarea
                ref={inputRef}
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="输入您的问题..."
                className="w-full px-5 py-4 border border-gray-300 rounded-2xl resize-none focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 shadow-sm bg-white/90 backdrop-blur-sm"
                rows={1}
                style={{ minHeight: '56px', maxHeight: '120px' }}
                disabled={loading}
              />
            </div>
            <button
              onClick={handleSendMessage}
              disabled={loading || !inputValue.trim()}
              className="h-14 w-14 bg-gradient-to-r from-blue-500 to-indigo-500 text-white rounded-2xl hover:from-blue-600 hover:to-indigo-600 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 shadow-lg hover:shadow-xl transform hover:scale-105 flex items-center justify-center"
            >
              {loading ? (
                <svg className="w-6 h-6 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
              ) : (
                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
                </svg>
              )}
            </button>
          </div>
          <p className="text-xs text-gray-500 text-center mt-3">
            按 Enter 发送消息，Shift + Enter 换行
          </p>
        </div>
      </div>

      <style jsx>{`
        @keyframes fade-in {
          from {
            opacity: 0;
            transform: translateY(10px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }
        .animate-fade-in {
          animation: fade-in 0.3s ease-out;
        }
        .line-clamp-2 {
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
      `}</style>
    </div>
  );
};

export default ChatInterface;