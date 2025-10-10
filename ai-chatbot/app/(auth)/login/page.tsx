"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useSession } from "next-auth/react";
import { useActionState, useEffect, useState } from "react";

import { AuthForm } from "@/components/auth-form";
import { SubmitButton } from "@/components/submit-button";
import { toast } from "@/components/toast";
import { type LoginActionState, login } from "../actions";

// 初始状态
const initialState: LoginActionState = {
  status: "idle",
};

export default function Page() {
  const router = useRouter();
  // 使用 useSession hook 来获取和跟踪 session 状态
  const { data: session, status: sessionStatus } = useSession();

  const [email, setEmail] = useState("");
  const [isSuccessful, setIsSuccessful] = useState(false);

  // useActionState 保持不变
  const [state, formAction] = useActionState<LoginActionState, FormData>(
    login,
    initialState
  );

  // 第一个 useEffect：处理 Server Action 返回的状态，用于显示 toast 和更新 UI
  useEffect(() => {
    if (state.status === "failed") {
      toast({
        type: "error",
        description: "Invalid credentials!",
      });
    } else if (state.status === "invalid_data") {
      toast({
        type: "error",
        description: "Failed validating your submission!",
      });
    } else if (state.status === "success") {
      // 登录成功，设置成功状态，等待 session 更新
      setIsSuccessful(true);
      toast({
        type: "success",
        description: "Login successful! Redirecting...",
      });
    }
  }, [state.status]);


  // ✅✅✅ --- 第二个 useEffect：处理登录成功后的跳转 --- ✅✅✅
  useEffect(() => {
    // 当 session 状态变为 'authenticated' 并且我们拿到了 user.id
    if (sessionStatus === 'authenticated' && session?.user?.id) {
      const userId = session.user.id;
      console.log(`Session updated. User ID: ${userId}. Redirecting...`);
      // 执行最终的跳转
      // router.push(`/chat/${userId}`);
      router.push(`/`);
    }
  }, [session, sessionStatus, router]);


  const handleSubmit = (formData: FormData) => {
    setEmail(formData.get("email") as string);
    formAction(formData);
  };

  return (
    <div className="flex h-dvh w-screen items-start justify-center bg-background pt-12 md:items-center md:pt-0">
      <div className="flex w-full max-w-md flex-col gap-12 overflow-hidden rounded-2xl">
        <div className="flex flex-col items-center justify-center gap-2 px-4 text-center sm:px-16">
          <h3 className="font-semibold text-xl dark:text-zinc-50">Sign In</h3>
          <p className="text-gray-500 text-sm dark:text-zinc-400">
            Use your email and password to sign in
          </p>
        </div>
        <AuthForm action={handleSubmit} defaultEmail={email}>
          <SubmitButton isSuccessful={isSuccessful}>Sign in</SubmitButton>
          <p className="mt-4 text-center text-gray-600 text-sm dark:text-zinc-400">
            {"Don't have an account? "}
            <Link
              className="font-semibold text-gray-800 hover:underline dark:text-zinc-200"
              href="/register"
            >
              Sign up
            </Link>
            {" for free."}
          </p>
        </AuthForm>
      </div>
    </div>
  );
}