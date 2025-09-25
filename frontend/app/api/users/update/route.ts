import { NextResponse } from "next/server";

export async function PUT(request: Request) {
  try {
    const { nickname, username } = await request.json();
    // 调用后端更新昵称接口
    const backendResponse = await fetch(
      "http://localhost:8080/api/user/update",
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nickname, username }),
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
