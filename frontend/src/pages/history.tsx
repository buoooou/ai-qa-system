import { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { AuthUtils } from '@/utils/auth';
import { qaApi } from '@/utils/api';
import { QuestionResponse, User } from '@/types';

export default function History() {
  const router = useRouter();
  const [user, setUser] = useState<User | null>(null);
  const [history, setHistory] = useState<QuestionResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  useEffect(() => {
    if (!AuthUtils.isAuthenticated()) {
      router.push('/login');
      return;
    }

    const currentUser = AuthUtils.getUser();
    if (currentUser) {
      setUser(currentUser);
      loadHistory(currentUser.id, 0);
    }
  }, [router]);

  const loadHistory = async (userId: number, pageNum: number) => {
    try {
      const response = await qaApi.getUserHistory(userId, pageNum, 10);
      if (response.code === 200) {
        if (pageNum === 0) {
          setHistory(response.data);
        } else {
          setHistory(prev => [...prev, ...response.data]);
        }
        setHasMore(response.data.length === 10);
      }
    } catch (error) {
      console.error('Failed to load history:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const loadMore = () => {
    if (user && hasMore) {
      const nextPage = page + 1;
      setPage(nextPage);
      loadHistory(user.id, nextPage);
    }
  };

  const handleDelete = async (qaId: number) => {
    if (!window.confirm('确定要删除这条记录吗？')) return;

    try {
      await qaApi.deleteQuestion(qaId);
      setHistory(prev => prev.filter(item => item.id !== qaId));
      alert('删除成功');
    } catch (error) {
      alert('删除失败');
    }
  };

  if (!user) return null;

  return (
    <div className="min-h-screen">
      {/* Header */}
      <header className="header-blur sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-6">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-gradient-to-r from-purple-400 to-pink-400 rounded-full flex items-center justify-center">
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <h1 className="text-2xl font-bold text-white">问答历史</h1>
            </div>
            <div className="flex items-center space-x-6">
              <button
                onClick={() => router.push('/')}
                className="text-white/90 hover:text-white transition-colors duration-200 font-medium flex items-center space-x-1"
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                </svg>
                <span>返回聊天</span>
              </button>
              <button
                onClick={() => AuthUtils.logout()}
                className="bg-red-500/80 backdrop-blur text-white px-4 py-2 rounded-full hover:bg-red-600/90 transition-all duration-200 font-medium flex items-center space-x-1"
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                </svg>
                <span>退出登录</span>
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Content */}
      <div className="max-w-6xl mx-auto p-6">
        {isLoading ? (
          <div className="text-center py-16 fade-in">
            <div className="loading-animation mx-auto mb-4"></div>
            <div className="text-white/80 text-lg">正在加载历史记录...</div>
          </div>
        ) : history.length === 0 ? (
          <div className="text-center py-16 fade-in">
            <div className="w-20 h-20 bg-white/20 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-10 h-10 text-white/60" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
              </svg>
            </div>
            <div className="text-white/80 text-lg">暂无问答记录</div>
            <p className="text-white/60 mt-2">开始你的第一次AI对话吧！</p>
          </div>
        ) : (
          <>
            <div className="space-y-6">
              {history.map((item, index) => (
                <div 
                  key={item.id} 
                  className="glass-effect rounded-2xl p-6 fade-in"
                  style={{animationDelay: `${index * 0.1}s`}}
                >
                  <div className="flex justify-between items-start mb-4">
                    <div className="flex-1">
                      <div className="flex items-start space-x-3 mb-3">
                        <div className="w-8 h-8 bg-gradient-to-r from-pink-500 to-rose-500 rounded-full flex items-center justify-center flex-shrink-0">
                          <span className="text-white font-bold text-sm">Q</span>
                        </div>
                        <h3 className="text-lg font-medium text-white leading-relaxed">
                          {item.question}
                        </h3>
                      </div>
                      <div className="text-sm text-white/60 flex flex-wrap gap-4 ml-11">
                        <span className="flex items-center space-x-1">
                          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                          </svg>
                          <span>{new Date(item.createTime).toLocaleString('zh-CN')}</span>
                        </span>
                        <span className="flex items-center space-x-1">
                          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
                          </svg>
                          <span>{item.responseTime}ms</span>
                        </span>
                        <span className="flex items-center space-x-1">
                          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
                          </svg>
                          <span>{item.modelVersion}</span>
                        </span>
                      </div>
                    </div>
                    <button
                      onClick={() => handleDelete(item.id)}
                      className="text-red-300 hover:text-red-100 transition-colors duration-200 p-2 hover:bg-red-500/20 rounded-lg"
                      title="删除记录"
                    >
                      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </div>

                  <div className="bg-white/10 backdrop-blur rounded-xl p-4 border border-white/20">
                    <div className="flex items-center space-x-2 mb-3">
                      <div className="w-6 h-6 bg-gradient-to-r from-blue-500 to-purple-600 rounded-full flex items-center justify-center">
                        <span className="text-white font-bold text-xs">A</span>
                      </div>
                      <h4 className="font-medium text-white/90">AI 回答:</h4>
                    </div>
                    <p className="text-white/80 whitespace-pre-wrap leading-relaxed ml-8">{item.answer}</p>
                  </div>
                </div>
              ))}
            </div>

            {hasMore && (
              <div className="text-center mt-8 fade-in">
                <button
                  onClick={loadMore}
                  className="gradient-button text-white px-8 py-3 rounded-xl font-medium flex items-center space-x-2 mx-auto"
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                  </svg>
                  <span>加载更多</span>
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}