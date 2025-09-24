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
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-4">
            <h1 className="text-2xl font-bold text-gray-900">问答历史</h1>
            <div className="flex items-center space-x-4">
              <button
                onClick={() => router.push('/')}
                className="text-blue-600 hover:text-blue-800"
              >
                返回聊天
              </button>
              <button
                onClick={() => AuthUtils.logout()}
                className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
              >
                退出登录
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Content */}
      <div className="max-w-6xl mx-auto p-6">
        {isLoading ? (
          <div className="text-center py-12">
            <div className="text-gray-600">加载中...</div>
          </div>
        ) : history.length === 0 ? (
          <div className="text-center py-12">
            <div className="text-gray-600">暂无问答记录</div>
          </div>
        ) : (
          <>
            <div className="space-y-6">
              {history.map((item) => (
                <div key={item.id} className="bg-white rounded-lg shadow p-6">
                  <div className="flex justify-between items-start mb-4">
                    <div className="flex-1">
                      <h3 className="text-lg font-medium text-gray-900 mb-2">
                        问题: {item.question}
                      </h3>
                      <div className="text-sm text-gray-500 flex space-x-4">
                        <span>时间: {new Date(item.createTime).toLocaleString()}</span>
                        <span>响应时间: {item.responseTime}ms</span>
                        <span>模型: {item.modelVersion}</span>
                      </div>
                    </div>
                    <button
                      onClick={() => handleDelete(item.id)}
                      className="text-red-600 hover:text-red-800 ml-4"
                    >
                      删除
                    </button>
                  </div>

                  <div className="bg-gray-50 rounded-lg p-4">
                    <h4 className="font-medium text-gray-700 mb-2">答案:</h4>
                    <p className="text-gray-800 whitespace-pre-wrap">{item.answer}</p>
                  </div>
                </div>
              ))}
            </div>

            {hasMore && (
              <div className="text-center mt-8">
                <button
                  onClick={loadMore}
                  className="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600"
                >
                  加载更多
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}