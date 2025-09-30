import Image from "next/image";
import Link from 'next/link';

export default function Home() {
  return (
    <div className="font-sans grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 bg-slate-900 text-foreground">
      <main className="flex flex-col gap-[32px] row-start-2 items-center sm:items-center">
        <div className="text-center">
          <div className="mx-auto h-16 w-16 bg-primary rounded-full flex items-center justify-center mb-6">
            <svg
              className="h-8 w-8 text-white"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"
              />
            </svg>
          </div>
          <h1 className="text-4xl font-bold text-foreground mb-4">AI智能问答系统</h1>
          <p className="text-lg text-foreground/80 mb-8">获取AI驱动的智能问答体验</p>
        </div>
        
        <div className="flex gap-4 items-center flex-col sm:flex-row">
          <Link
            href="/login"
            className="rounded-full border border-solid border-transparent transition-colors flex items-center justify-center bg-primary text-white gap-2 hover:bg-primary-variant font-medium text-sm sm:text-base h-12 sm:h-14 px-6 sm:px-8 sm:w-auto"
          >
            立即开始
          </Link>
          <Link
            href="/register"
            className="rounded-full border border-solid border-primary/60 transition-colors flex items-center justify-center hover:bg-primary/10 font-medium text-sm sm:text-base h-12 sm:h-14 px-6 sm:px-8 w-full sm:w-auto md:w-[158px]"
          >
            注册账号
          </Link>
        </div>
      </main>
      <footer className="row-start-3 flex gap-[24px] flex-wrap items-center justify-center text-foreground/60">
        <p>© 2023 AI智能问答系统</p>
        <a
          className="flex items-center gap-2 hover:underline hover:underline-offset-4 hover:text-primary"
          href="#"
          target="_blank"
          rel="noopener noreferrer"
        >
          隐私政策
        </a>
        <a
          className="flex items-center gap-2 hover:underline hover:underline-offset-4 hover:text-primary"
          href="#"
          target="_blank"
          rel="noopener noreferrer"
        >
          使用条款
        </a>
      </footer>
    </div>
  );
}
