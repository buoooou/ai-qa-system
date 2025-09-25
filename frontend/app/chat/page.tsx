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
}

interface HistorySession {
  id: string;
  title: string;
}

export default function ChatPage() {
  const router = useRouter();
  const [messages, setMessages] = useState<ChatMessage[]>([
    { id: "1", role: "assistant", content: "您好！请输入消息。" },
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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    console.log("handleSubmit called");
    if (!input.trim()) return;

    // 用户发送消息
    const userMessage: ChatMessage = {
      id: Date.now().toString(),
      role: "user",
      content: input,
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

      console.log("after save Data:", response);
      if (response.status != 200 && response.data.data.answer === null) {
        const errorMessage = "发送消息失败";
        setIsThinking(false);
        toast.error(errorMessage);
        const errorMessageObj: ChatMessage = {
          id: Date.now().toString(),
          role: "assistant",
          content: "系统繁忙，请稍后再试",
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
      },
    ]);
  };

  const handleLoadHistory = async () => {
    // 加载历史会话
    console.log("handleLoadHistory:11312");
    try {
      // const response = await axiosAI.post("/api/qa/history", {
      //   userId: localStorage.getItem("userid"),
      // });
      const response = await axiosAI.get("/api/qa/history", {
        params: {
          userId: localStorage.getItem("userid"),
        },
      });

      console.log("after load Data:", response);
      // if (response != null) {
      //   toast.success("会话加载成功");
      //   const formattedMessages: ChatMessage[] = [];
      //   response.forEach((q: string, index: number) => {
      //     formattedMessages.push({
      //       id: `q-${index + 1}`,
      //       role: "user",
      //       answer: q,
      //     });
      //     if (response[index]) {
      //       formattedMessages.push({
      //         id: `a-${index + 1}`,
      //         role: "assistant",
      //         answer: response[index],
      //       });
      //     }
      //   });
      //   setMessages(formattedMessages);
      // } else {
      //   toast.error("加载会话失败");
      // }
    } catch (error) {
      toast.error("网络错误，请重试");
      console.log(error);
    }
  };

  return (
    <div className={`flex h-screen overflow-visible bg-slate-900 text-sky-100`}>
      {/* 侧边栏 */}
      <div
        className={`w-64 p-4 border-r flex flex-col h-full bg-slate-800 border-sky-700/30`}
      >
        <button
          onClick={handleNewSession}
          className={`w-full p-3 mb-4 rounded-lg hover:opacity-90 transition-colors 
            bg-sky-600 text-white hover:bg-sky-700`}
        >
          新加会话
        </button>
        <div className={`mb-2 font-semibold text-sky-300/80`}></div>
        <div className="space-y-2 flex-grow whitespace-nowrap truncate"></div>
        {/* 用户昵称显示区域 */}
        <div
          className={`p-4 rounded-lg bg-slate-800/60 border-t border-sky-700/30`}
          onClick={(e) => {
            e.stopPropagation();
            const menus = document.querySelectorAll(".user-menu");
            menus.forEach((menu) => menu.classList.add("hidden"));
          }}
        >
          <div className="flex items-center justify-between mb-4 spaace-y-2 whitespace-nowrap p-2 rounded-full mx-auto">
            {isEditingNickname ? (
              <input
                type="text"
                value={nicknameInput}
                onChange={(e) => setNicknameInput(e.target.value)}
                onBlur={handleNicknameSubmit}
                onKeyDown={(e) => e.key === "Enter" && handleNicknameSubmit()}
                className={`font-semibold w-full text-sky-300 bg-slate-700 p-2 rounded border-none outline-none`}
                autoFocus
              />
            ) : (
              <div className={`font-semibold text-sky-300 p-2 rounded`}>
                {nickname}
              </div>
            )}
            <div className="relative">
              <button
                className={`p-1 rounded-full w-8 h-8 flex items-center justify-center cursor-pointer 
                  bg-slate-700 text-sky-300 hover:bg-slate-600`}
                onClick={(e) => {
                  e.stopPropagation();
                  const menu = e.currentTarget
                    .nextElementSibling as HTMLElement;
                  menu.classList.toggle("hidden");
                }}
              >
                ...
              </button>
              <div
                className={`absolute right-0 bottom-full mb-2 w-48 rounded-md shadow-lg 
                  bg-slate-700 z-50 hidden user-menu`}
              >
                <div className="py-1">
                  <button
                    className={`block w-full text-left px-4 py-2 text-sky-300 hover:bg-slate-600`}
                    onClick={() => {
                      setIsEditingNickname(true);
                    }}
                  >
                    更换昵称
                  </button>
                  <button
                    className={`block w-full text-left px-4 py-2 text-sky-300 hover:bg-slate-600`}
                    onClick={() => handleLogout()}
                  >
                    退出
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* 主聊天区域 */}
      <div className="flex-1 flex flex-col max-w-3xl p-4 mx-auto">
        <div className="flex-grow overflow-y-auto mb-4 space-y-4">
          {messages.map((message: ChatMessage) => (
            <div key={message.id} className={`p-4 rounded-lg bg-slate-700/50`}>
              <span className={`font-bold text-sky-300`}>
                {message.role === "user" ? "You: " : "AI: "}
              </span>
              <span className={`text-sky-100`}>{message.content}</span>
            </div>
          ))}
        </div>

        <form onSubmit={handleSubmit} className="flex gap-2">
          <input
            type="text"
            value={input}
            onChange={handleInputChange}
            placeholder="输入消息..."
            className={`flex-grow p-3 border rounded-lg focus:outline-none focus:ring-2 bg-slate-700/50 border-sky-700/50 focus:ring-sky-400`}
          />
          <button
            type="submit"
            className={`p-3 text-white rounded-lg hover:opacity-90 transition-colors bg-sky-600 hover:bg-sky-700`}
          >
            发送
          </button>
        </form>
      </div>
    </div>
  );
}
