"use client";
import { useState, useEffect, useRef } from "react";
import { useRouter } from "next/navigation";
import toast from "react-hot-toast";
import axios from "../commons/axios";
import axiosAI from "../commons/axiosAI";

interface ChatMessage {
  id: string;
  role: "user" | "assistant";
  content: string;
  timestamp?: string;
}

interface HistorySession {
  id: string;
  title: string;
  lastMessage?: string;
  timestamp?: string;
  unreadCount?: number;
}

export default function ChatPage() {
  const router = useRouter();
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      id: "1", 
      role: "assistant", 
      content: "您好！我是您的智能助手。有什么我可以帮助您的吗？",
      timestamp: new Date().toLocaleTimeString()
    },
  ]);
  const [input, setInput] = useState("");
  const [nickname, setNickname] = useState<string>(
    typeof window !== "undefined"
      ? localStorage.getItem("nickname") || "用户"
      : "用户"
  );
  const [isEditingNickname, setIsEditingNickname] = useState(false);
  const [nicknameInput, setNicknameInput] = useState(nickname);
  const [isThinking, setIsThinking] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [historySessions, setHistorySessions] = useState<HistorySession[]>([
    { id: "1", title: "项目规划讨论", lastMessage: "您的项目计划看起来很全面", timestamp: "10:30" },
    { id: "2", title: "技术选型咨询", lastMessage: "我推荐使用React和Node.js", timestamp: "昨天", unreadCount: 2 },
    { id: "3", title: "代码优化建议", lastMessage: "您可以尝试使用Hooks重构", timestamp: "周一" }
  ]);
  const [searchTerm, setSearchTerm] = useState("");
  const [showMobileSidebar, setShowMobileSidebar] = useState(false);
  
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const sidebarRef = useRef<HTMLDivElement>(null);

  // 自动滚动到最新消息
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleLogout = async () => {
    localStorage.clear();
    router.push("/login");
  };

  const handleNicknameSubmit = async () => {
    if (nicknameInput.trim() === nickname) {
      setIsEditingNickname(false);
      return;
    }

    try {
      const response = await axios.post("/api/user/updateNickName", {
        nickname: nicknameInput,
        username: localStorage.getItem("username"),
      });
      if (response.data.code === 200) {
        localStorage.setItem("nickname", nicknameInput);
        setNickname(nicknameInput);
        toast.success("昵称更新成功");
      } else {
        toast.error(response.data.message || "昵称更新失败");
      }
    } catch (error) {
      toast.error("网络错误，请重试");
    } finally {
      setIsEditingNickname(false);
    }
  };

  const initialized = useRef(false);

  useEffect(() => {
    if (typeof window !== "undefined" && !initialized.current) {
      const handleStorageChange = () => {
        setNickname(localStorage.getItem("nickname") || "用户");
      };
      window.addEventListener("storage", handleStorageChange);
      handleInitSession();
      initialized.current = true;
      return () => window.removeEventListener("storage", handleStorageChange);
    }
  }, []);

  // 点击页面其他区域关闭用户菜单
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (sidebarRef.current && !sidebarRef.current.contains(event.target as Node)) {
        setShowUserMenu(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [sidebarRef]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim()) return;

    // 用户发送消息
    const userMessage: ChatMessage = {
      id: Date.now().toString(),
      role: "user",
      content: input,
      timestamp: new Date().toLocaleTimeString()
    };
    setMessages([...messages, userMessage]);
    setInput("");
    setIsThinking(true); // 显示“思考中...”动画

    try {
      // 调用发送消息接口
      const response = await axiosAI.post("/api/qa/save", {
        userId: localStorage.getItem("userid"),
        question: input,
        nickname: nickname,
      });

      if (response.status != 200 && response.data.data.answer === null) {
        const errorMessage = "发送消息失败";
        setIsThinking(false);
        toast.error(errorMessage);
        const errorMessageObj: ChatMessage = {
          id: Date.now().toString(),
          role: "assistant",
          content: "系统繁忙，请稍后再试",
          timestamp: new Date().toLocaleTimeString()
        };
        setMessages((prevMessages) => [...prevMessages, errorMessageObj]);
        return;
      }

      // 获取完整响应内容
      const fullResponse = 
        response.data.answer || response.data.message || "AI回复内容";

      // 创建AI回复的初始消息
      const aiMessageId = Date.now().toString();
      const aiMessage: ChatMessage = {
        id: aiMessageId,
        role: "assistant",
        content: "",
        timestamp: new Date().toLocaleTimeString()
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

        const chunk = fullResponse.substring(
          chunkIndex,
          chunkIndex + chunkSize
        );
        chunkIndex += chunkSize;

        // 更新AI回复内容
        setMessages((prevMessages) =>
          prevMessages.map((msg) =>
            msg.id === aiMessageId
              ? { ...msg, content: msg.content + chunk }
              : msg
          )
        );
      }, 100); // 每100毫秒更新一次
    } catch (error) {
      toast.error("网络错误，请重试");
      // 添加网络错误提示到消息列表
      const errorMessageObj: ChatMessage = {
        id: Date.now().toString(),
        role: "assistant",
        content: "网络异常，请检查连接后重试",
        timestamp: new Date().toLocaleTimeString()
      };
      setMessages((prevMessages) => [...prevMessages, errorMessageObj]);
      setIsThinking(false); // 隐藏“思考中...”动画
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInput(e.target.value);
  };

  const handleNewSession = async () => {
    setMessages([
      {
        id: "new_chat_" + Date.now(),
        role: "assistant",
        content: "我是AI助手，你有什么问题？",
        timestamp: new Date().toLocaleTimeString()
      },
    ]);
    toast.success("新会话创建成功");
  };

  const handleInitSession = async () => {
    setMessages([
      {
        id: "new_chat_" + Date.now(),
        role: "assistant",
        content: "我是AI助手，你有什么问题？",
        timestamp: new Date().toLocaleTimeString()
      },
    ]);
  };

  const handleLoadHistory = async () => {
    // 加载历史会话
    try {
      const response = await axiosAI.get("/api/qa/history", {
        params: {
          userId: localStorage.getItem("userid"),
        },
      });

      console.log("历史会话数据:", response);
      // 实际项目中可以根据API返回的数据更新历史会话列表
    } catch (error) {
      toast.error("加载历史会话失败");
      console.log(error);
    }
  };

  const handleLoadSession = (sessionId: string) => {
    // 在实际项目中，这里应该根据sessionId加载对应的会话消息
    toast.success(`加载会话: ${historySessions.find(s => s.id === sessionId)?.title}`);
    // 在移动端模式下，加载会话后关闭侧边栏
    if (window.innerWidth < 768) {
      setShowMobileSidebar(false);
    }
  };

  // 移动端侧边栏切换按钮
  const MobileSidebarToggle = () => (
    <button 
      onClick={() => setShowMobileSidebar(!showMobileSidebar)}
      className="md:hidden fixed top-4 left-4 z-50 p-2 rounded-full bg-slate-800 border border-slate-700 text-sky-300"
    >
      <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
      </svg>
    </button>
  );

  return (
    <div className="flex h-screen overflow-hidden bg-slate-900 text-sky-100">
      <MobileSidebarToggle />
      
      {/* 侧边栏 */}
      <div
        ref={sidebarRef}
        className={`
          w-64 md:w-72 p-4 border-r flex flex-col h-full bg-slate-800 border-slate-700/30 transition-transform duration-300 ease-in-out
          ${showMobileSidebar ? 'fixed inset-y-0 left-0 z-40' : '-translate-x-full md:translate-x-0'}
        `}
      >
        {/* 侧边栏顶部 */}
        <div className="mb-6">
          <h1 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
            <svg className="h-6 w-6 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path 
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={1.5}
                d="M12 4a4 4 0 1 1 0 8 4 4 0 0 1 0-8zM12 14c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zM5 19h14v-2H5v2zM5 7h14v2H5V7z"
              />
            </svg>
            智能助手
          </h1>
          
          {/* 新建会话按钮 */}
          <button
            onClick={handleNewSession}
            className={`w-full p-3 rounded-lg hover:opacity-90 transition-all duration-300 flex items-center justify-center gap-2
              bg-primary text-white hover:bg-primary/90 hover:shadow-lg hover:shadow-primary/20`}
          >
            <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
            </svg>
            新会话
          </button>
        </div>
        
        {/* 搜索框 */}
        <div className="mb-6">
          <input
            type="text"
            placeholder="搜索会话..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full p-2.5 pl-9 rounded-lg bg-slate-700/50 border border-slate-600 text-sky-100 focus:outline-none focus:ring-2 focus:ring-primary"
          />
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <svg className="h-4 w-4 text-sky-400/60" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
        </div>
        
        {/* 历史会话列表 */}
        <div className="mb-2 font-medium text-xs uppercase text-slate-400 tracking-wider mb-3">
          最近会话
        </div>
        <div className="space-y-1 flex-grow overflow-y-auto pr-2">
          {historySessions.map((session) => (
            <div
              key={session.id}
              className={`p-3 rounded-lg cursor-pointer transition-all duration-200 hover:bg-slate-700/50 flex flex-col gap-1.5 border border-transparent hover:border-slate-600/50`}
              onClick={() => handleLoadSession(session.id)}
            >
              <div className="flex justify-between items-center gap-2">
                <h3 className="font-medium text-sky-200 truncate">{session.title}</h3>
                <span className="text-xs text-slate-400 whitespace-nowrap">{session.timestamp}</span>
              </div>
              <p className="text-sm text-slate-400 line-clamp-1">{session.lastMessage}</p>
              {session.unreadCount && session.unreadCount > 0 && (
                <span className="absolute -top-1 -right-1 bg-primary text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                  {session.unreadCount}
                </span>
              )}
            </div>
          ))}
        </div>
        
        {/* 用户信息区域 */}
        <div className={`mt-4 p-4 rounded-lg bg-slate-700/30 border border-slate-600/50`}>
          <div className="flex items-center justify-between gap-3">
            <div className="relative flex-1">
              {isEditingNickname ? (
                <input
                  type="text"
                  value={nicknameInput}
                  onChange={(e) => setNicknameInput(e.target.value)}
                  onBlur={handleNicknameSubmit}
                  onKeyDown={(e) => e.key === "Enter" && handleNicknameSubmit()}
                  className={`font-medium w-full text-white bg-slate-700 p-2 rounded border border-slate-500 outline-none focus:ring-1 focus:ring-primary`}
                  autoFocus
                />
              ) : (
                <div className={`font-medium text-white`}>
                  {nickname}
                </div>
              )}
              <div className="text-xs text-slate-400">在线</div>
            </div>
            
            {/* 用户菜单按钮 */}
            <div className="relative">
              <button
                className={`p-2 rounded-full w-9 h-9 flex items-center justify-center cursor-pointer transition-colors duration-200
                  bg-slate-700 text-slate-300 hover:bg-slate-600`}
                onClick={() => setShowUserMenu(!showUserMenu)}
              >
                <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z" />
                </svg>
              </button>
              
              {/* 用户菜单 */}
              {showUserMenu && (
                <div className={`absolute right-0 bottom-full mb-2 w-48 rounded-lg shadow-xl overflow-hidden
                  bg-slate-700 z-50 border border-slate-600 animate-in fade-in duration-200`}>
                  <div className="py-1">
                    <button
                      className={`block w-full text-left px-4 py-2.5 text-slate-200 hover:bg-slate-600 transition-colors duration-150`}
                      onClick={() => setIsEditingNickname(true)}
                    >
                      <svg className="h-4 w-4 inline mr-2 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                      </svg>
                      修改昵称
                    </button>
                    <button
                      className={`block w-full text-left px-4 py-2.5 text-slate-200 hover:bg-slate-600 transition-colors duration-150`}
                      onClick={handleLoadHistory}
                    >
                      <svg className="h-4 w-4 inline mr-2 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                      </svg>
                      加载历史
                    </button>
                    <div className="border-t border-slate-600 my-1"></div>
                    <button
                      className={`block w-full text-left px-4 py-2.5 text-red-300 hover:bg-red-900/20 transition-colors duration-150`}
                      onClick={handleLogout}
                    >
                      <svg className="h-4 w-4 inline mr-2 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H3" />
                      </svg>
                      退出登录
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* 主聊天区域 */}
      <div className="flex-1 flex flex-col">
        {/* 顶部聊天信息栏 */}
        <div className="border-b border-slate-700/30 bg-slate-800/50 py-3 px-6 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="hidden md:flex items-center justify-center h-10 w-10 rounded-full bg-primary/20 text-primary">
              <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path 
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={1.5}
                  d="M12 4a4 4 0 1 1 0 8 4 4 0 0 1 0-8zM12 14c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zM5 19h14v-2H5v2zM5 7h14v2H5V7z"
                />
              </svg>
            </div>
            <div>
              <h2 className="font-medium text-white">智能助手</h2>
              <div className="text-xs text-slate-400">正在运行中</div>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <button className="p-2 text-slate-400 hover:text-white hover:bg-slate-700/30 rounded-full transition-colors duration-200">
              <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </button>
            <button className="p-2 text-slate-400 hover:text-white hover:bg-slate-700/30 rounded-full transition-colors duration-200">
              <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </button>
          </div>
        </div>
        
        {/* 消息列表区域 */}
        <div className="flex-grow overflow-y-auto p-6 bg-slate-900/70 space-y-6">
          {/* 日期分隔符 */}
          <div className="flex justify-center">
            <div className="px-4 py-1 rounded-full bg-slate-800 text-xs text-slate-400">
              {new Date().toLocaleDateString()}
            </div>
          </div>
          
          {/* 消息列表 */}
          {messages.map((message: ChatMessage) => (
            <div 
              key={message.id} 
              className={`flex ${message.role === 'user' ? 'justify-end' : 'justify-start'}`}
            >
              <div className={`max-w-[85%] ${message.role === 'user' ? 'order-2' : 'order-1'}`}>
                <div className={`
                  p-4 rounded-xl shadow-sm relative
                  ${message.role === 'user' 
                    ? 'bg-primary text-white rounded-br-none' 
                    : 'bg-slate-800 text-slate-100 rounded-bl-none border border-slate-700'
                  }
                `}>
                  {/* 用户头像（仅AI） */}
                  {message.role === 'assistant' && (
                    <div className={`absolute -left-10 top-0 flex items-center justify-center h-8 w-8 rounded-full bg-primary/20 text-primary order-0`}>
                      <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path 
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={1.5}
                          d="M12 4a4 4 0 1 1 0 8 4 4 0 0 1 0-8z"
                        />
                      </svg>
                    </div>
                  )}
                  
                  {/* 消息内容 */}
                  <p className="whitespace-pre-wrap">{message.content}</p>
                  
                  {/* 消息时间 */}
                  <div className={`
                    text-xs mt-1 flex justify-end
                    ${message.role === 'user' ? 'text-white/60' : 'text-slate-400'}
                  `}>
                    {message.timestamp}
                  </div>
                </div>
              </div>
            </div>
          ))}
          
          {/* 正在思考指示器 */}
          {isThinking && (
            <div className="flex justify-start">
              <div className="max-w-[85%]">
                <div className="p-4 rounded-xl rounded-bl-none bg-slate-800 text-slate-100 border border-slate-700 shadow-sm">
                  <div className="flex items-center space-x-2">
                    <div className="h-2 w-2 rounded-full bg-slate-400 animate-pulse"></div>
                    <div className="h-2 w-2 rounded-full bg-slate-400 animate-pulse delay-75"></div>
                    <div className="h-2 w-2 rounded-full bg-slate-400 animate-pulse delay-150"></div>
                  </div>
                </div>
              </div>
            </div>
          )}
          
          {/* 滚动锚点 */}
          <div ref={messagesEndRef} />
        </div>
        
        {/* 底部输入区域 */}
        <div className="border-t border-slate-700/30 bg-slate-800/80 p-4">
          <form onSubmit={handleSubmit} className="flex flex-col gap-3">
            {/* 功能按钮栏 */}
            <div className="flex items-center gap-2 text-slate-400">
              <button type="button" className="p-2 hover:text-white hover:bg-slate-700/30 rounded-full transition-colors duration-200">
                <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </button>
              <button type="button" className="p-2 hover:text-white hover:bg-slate-700/30 rounded-full transition-colors duration-200">
                <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </button>
              <button type="button" className="p-2 hover:text-white hover:bg-slate-700/30 rounded-full transition-colors duration-200">
                <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                </svg>
              </button>
            </div>
            
            {/* 输入框和发送按钮 */}
            <div className="flex gap-3">
              <input
                type="text"
                value={input}
                onChange={handleInputChange}
                placeholder="输入消息..."
                className={`flex-grow p-3.5 pr-12 rounded-xl focus:outline-none focus:ring-2 bg-slate-700 border border-slate-600 text-slate-100 focus:ring-primary transition-all duration-200`}
                disabled={isThinking}
              />
              <button
                type="submit"
                disabled={isThinking || !input.trim()}
                className={`px-5 py-3 rounded-xl hover:opacity-90 transition-all duration-300 flex items-center justify-center
                  ${isThinking || !input.trim() 
                    ? 'bg-slate-700 text-slate-400 cursor-not-allowed'
                    : 'bg-primary text-white hover:bg-primary/90 hover:shadow-lg hover:shadow-primary/20'
                  }`}
              >
                {isThinking ? (
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                ) : (
                  <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 8l4 4m0 0l-4 4m4-4H3" />
                  </svg>
                )}
              </button>
            </div>
            
            {/* 底部提示 */}
            <div className="text-xs text-slate-500 text-center">
              按 Enter 发送消息，Shift + Enter 换行
            </div>
          </form>
        </div>
      </div>
      
      {/* 移动端遮罩 */}
      {showMobileSidebar && (
        <div 
          className="md:hidden fixed inset-0 bg-black/60 backdrop-blur-sm z-30"
          onClick={() => setShowMobileSidebar(false)}
        />
      )}
    </div>
  );
}
