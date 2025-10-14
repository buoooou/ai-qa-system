"use server";

import { z } from "zod";

import { signIn } from "./auth"; // 假设这是指向 auth.ts 的正确路径

const authFormSchema = z.object({
  email: z.string().email(),
  password: z.string().min(6),
});

export type LoginActionState = {
  status: "idle" | "in_progress" | "success" | "failed" | "invalid_data";
};

export const login = async (
  _: LoginActionState,
  formData: FormData
): Promise<LoginActionState> => {
  try {
    const validatedData = authFormSchema.parse({
      email: formData.get("email"),
      password: formData.get("password"),
    });

    // 调用 signIn 并将错误作为异常抛出，而不是返回结果
    // 这样 next-auth 的错误处理机制会更自然地工作
    await signIn("credentials", {
      email: validatedData.email,
      password: validatedData.password,
      redirect: false, // 非常重要，这样它会抛出错误而不是重定向
    });

    // 如果 signIn 没有抛出错误，就代表成功
    return { status: "success" };

  } catch (error: any) {
    
    // next-auth 在认证失败时会抛出一个特定类型的错误
    // 我们可以根据错误类型返回更具体的状态，但为了简单起见，统一返回 failed
    if (error.name === 'CredentialsSignin') {
        return { status: "failed" };
    }

    if (error instanceof z.ZodError) {
      return { status: "invalid_data" };
    }

    // 对于其他未知错误，也返回 failed
    return { status: "failed" };
  }
};

// register 函数保持不变
export type RegisterActionState = {
  status:
    | "idle"
    | "in_progress"
    | "success"
    | "failed"
    | "user_exists"
    | "invalid_data";
};

export const register = async (
  _: RegisterActionState,
  formData: FormData
): Promise<RegisterActionState> => {
    // ... 此函数逻辑保持不变 ...
    try {
        const validatedData = authFormSchema.parse({
          email: formData.get("email"),
          password: formData.get("password"),
        });
    
        const username = validatedData.email.split("@")[0];
    
        try {
          // signIn for register...
          await signIn("register", {
            email: validatedData.email,
            username,
            password: validatedData.password,
            nickname: username,
            redirect: false,
          });
          
          return { status: "success" };

        } catch (error: any) {
          // 在 next-auth v5 中，自定义错误通常通过 error.cause 来传递
          if (error.cause?.name === "USER_EXISTS") {
            return { status: "user_exists" };
          }
          return { status: "failed" };
        }
      } catch (error) {
        if (error instanceof z.ZodError) {
          return { status: "invalid_data" };
        }
    
        return { status: "failed" };
      }
};