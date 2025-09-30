import { NextResponse } from "next/server";

export async function POST(request: Request) {
  try {
    const { username, nickname, password } = await request.json();
    // 调用后端注册接口
    const backendResponse = await fetch(
      "http://localhost:8080/api/user/register",
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, nickname, password }),
      }
    );

    const data = await backendResponse.json();
    return NextResponse.json(
      {
        code: data.code,
        message: data.message,
        data: data.data,
      },
      { status: data.code }
    );
  } catch (error) {
    console.error("Logout error:", error);
    return NextResponse.json({ message: "服务器错误" }, { status: 500 });
  }
}
