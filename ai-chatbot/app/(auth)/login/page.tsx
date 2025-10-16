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
      // 登录成功，显示成功信息，但不设置按钮为成功状态
      // 按钮状态由useFormStatus()管理
      toast({
        type: "success",
        description: "Login successful! Redirecting...",
      });
    }
  }, [state.status]);


  // ✅✅✅ --- 处理登录成功后的跳转 --- ✅✅✅
  useEffect(() => {
    console.log("[Login] useEffect triggered:", {
      sessionStatus,
      hasSession: !!session,
      stateStatus: state.status,
      userId: session?.user?.id
    });

    // 优先检查 session 状态，如果已经认证就跳转
    if (sessionStatus === 'authenticated' && session?.user?.id) {
      console.log("[Login] Session authenticated, redirecting to home...");
      router.replace(`/`);
      return;
    }
  }, [session, sessionStatus, router]);

  // 单独处理登录成功后的跳转
  useEffect(() => {
    if (state.status === "success") {
      console.log("[Login] Login action returned success, checking session...");
      // 给 session 更新一些时间，然后检查
      const checkSessionAndRedirect = async () => {
        // 等待一段时间让 session 更新
        await new Promise(resolve => setTimeout(resolve, 500));

        // 强制刷新 session
        await router.refresh();

        // 再次等待一小段时间
        await new Promise(resolve => setTimeout(resolve, 200));

        // 检查 session 状态
        console.log("[Login] After refresh - sessionStatus:", sessionStatus, "session:", session);

        if (sessionStatus === 'authenticated') {
          console.log("[Login] Session confirmed, redirecting...");
          router.replace("/");
        } else {
          console.log("[Login] Session not yet authenticated, using window.location...");
          // 如果 session 还没更新，使用完整页面重载
          window.location.href = "/";
        }
      };

      checkSessionAndRedirect();
    }
  }, [state.status, router, sessionStatus, session]);


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
          <SubmitButton>Sign in</SubmitButton>
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