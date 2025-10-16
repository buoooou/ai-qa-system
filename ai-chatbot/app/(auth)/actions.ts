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
  console.log("[Actions] login action called");

  try {
    const validatedData = authFormSchema.parse({
      email: formData.get("email"),
      password: formData.get("password"),
    });

    console.log("[Actions] Validated data:", { email: validatedData.email });

    // 调用 signIn，不使用 redirect: false
    // 这样返回的 result 会包含 session 信息
    console.log("[Actions] Calling signIn...");

    const result = await signIn("credentials", {
      email: validatedData.email,
      password: validatedData.password,
      redirect: false, // 不自动重定向，我们需要检查结果
    });

    console.log("[Actions] signIn result:", {
      ok: result,
      error: result,
      status: result?.status,
      url: result?.url
    });

    // 检查 result 是否表示成功
    if (result && !result?.error) {
      console.log("[Actions] Login appears successful");
      return { status: "success" };
    } else {
      console.log("[Actions] Login failed with result:", result);
      throw new Error("Invalid credentials");
    }

  } catch (error: any) {
    console.error("[Actions] Login error:", error);
    
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