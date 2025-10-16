import { redirect } from "next/navigation";

export default function ChatPage() {
  // 重定向到根路径，因为聊天界面在根路径
  redirect("/");
}