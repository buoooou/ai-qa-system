import { NextResponse } from "next/server";

type RegisterRequestBody = {
  username: string;
  password: string;
};

export async function POST(request: Request) {
  try {
    const body = (await request.json()) as RegisterRequestBody;
    const { username, password } = body;

    if (!username || !password) {
      return NextResponse.json({ error: "用户名和密码不能为空" }, { status: 400 });
    }

    if (password.length < 6) {
      return NextResponse.json({ error: "密码长度至少6位" }, { status: 400 });
    }

    const backendBaseUrl = process.env.BACKEND_BASE_URL ?? "http://localhost:8080";

    const response = await fetch(`${backendBaseUrl}/api/user/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ username, password }),
    });

    if (!response.ok) {
      const text = await response.text();
      return NextResponse.json(
        { error: "注册失败", details: text },
        { status: response.status },
      );
    }

    const data = (await response.json()) as { token?: string };
    
    return NextResponse.json({
      token: data.token,
      user: { username },
    });
  } catch (error) {
    return NextResponse.json(
      { error: "注册失败", details: (error as Error).message },
      { status: 500 },
    );
  }
}

