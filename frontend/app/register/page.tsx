"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import toast from "react-hot-toast";
import axios from "../commons/axios";

export default function RegisterPage() {
  const [username, setUsername] = useState("");
  const [nickname, setNickname] = useState("");
  const [role, setRole] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (
      !username.trim() ||
      !nickname.trim() ||
      !role.trim() ||
      !password.trim() ||
      !confirmPassword.trim()
    ) {
      toast.error("所有字段不能为空");
      return;
    }

    if (password !== confirmPassword) {
      toast.error("两次输入的密码不一致");
      return;
    }

    // // 密码强度检测
    // const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*]).{8,}$/;
    // if (!passwordRegex.test(password)) {
    //   toast.error('密码强度不足，需包含大小写字母、数字和特殊字符，且至少8位');
    //   return;
    // }

    setIsLoading(true);
    try {
      // const response = await fetch('http://localhost:8080/api/user/register', {
      //   method: 'POST',
      //   headers: { 'Content-Type': 'application/json' },
      //   body: { username, nickname,role, password },
      // });

      const response = await axios.post("/api/user/register", {
        username: username,
        nickname: nickname,
        role: role,
        password: password,
      });
      console.log(response);
      if (response.data.code !== 200) {
        throw new Error(`注册失败: ${response.data.message}`);
      }

      toast.success("注册成功");
      router.push("/login");
    } catch (error) {
      toast.error("注册失败");
      console.error("注册失败:", error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100">
      <div className="max-w-md w-full space-y-8 p-8 bg-white rounded-xl shadow-lg">
        <h1 className="text-2xl font-bold mb-6 text-center">注册</h1>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              用户名
            </label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="请输入用户名"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              昵称
            </label>
            <input
              type="text"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="请输入昵称"
            />
          </div>

          <div>
            <label
              htmlFor="role"
              className="block text-sm font-medium text-gray-700"
            >
              角色选择：
            </label>
            <select
              id="role"
              name="role"
              required
              value={role}
              onChange={(e) => setRole(e.target.value)}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">请选择角色</option>
              <option value="user">普通用户</option>
              <option value="moderator">管理员</option>
              <option value="editor">编辑</option>
              <option value="guest">访客</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              密码
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="请输入密码"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              确认密码
            </label>
            <input
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="请再次输入密码"
            />
          </div>
          <button
            type="submit"
            disabled={isLoading}
            className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50"
          >
            {isLoading ? "注册中..." : "注册"}
          </button>
        </form>
        <div className="mt-4 text-center">
          <span className="text-sm text-gray-600">已有账号？</span>
          <a href="/login" className="text-sm text-blue-600 hover:underline">
            立即登录
          </a>
        </div>
      </div>
    </div>
  );
}
