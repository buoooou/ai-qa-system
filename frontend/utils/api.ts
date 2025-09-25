// 全局请求封装
import { toast } from "react-hot-toast";

type RequestOptions = {
  method?: "GET" | "POST" | "PUT" | "DELETE";
  headers?: Record<string, string>;
  body?: object;
};

export async function apiFetch(url: string, options: RequestOptions = {}) {
  try {
    // 从 localStorage 获取 Token
    const token = localStorage.getItem("token");

    // 合并请求头
    const headers = {
      "Content-Type": "application/json",
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options.headers,
    };

    // 发送请求
    const response = await fetch(url, {
      ...options,
      headers,
      body: options.body ? JSON.stringify(options.body) : undefined,
    });

    // 解析响应
    const data = await response.json();

    // 处理业务逻辑错误
    if (!response.ok || data.code !== 200) {
      throw new Error(data.message || "请求失败");
    }

    return data;
  } catch (error) {
    // 统一错误处理
    toast.error("网络错误");
    throw error;
  }
}
