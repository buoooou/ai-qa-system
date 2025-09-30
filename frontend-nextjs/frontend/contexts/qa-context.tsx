"use client"

import { createContext, useContext, useEffect, useState, type ReactNode } from "react"
import type { User, LoginRequest, RegisterRequest, AuthContextType } from "@/types/auth"
import { authAPI } from "@/lib/auth-api"
import { useToast } from "@/hooks/use-toast"
import { ChatMessage, QaContextType } from "@/types/chat"
import { qaAPI } from "@/lib/qa-api"

const QaContext = createContext<QaContextType | undefined>(undefined)

export function QaProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [token, setToken] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const { toast } = useToast()

  // Load token from localStorage on mount
  useEffect(() => {
    const savedToken = localStorage.getItem("auth_token")
    if (savedToken) {
      setToken(savedToken)
      // Try to get user info with saved token
      authAPI
        .getCurrentUser(savedToken)
        .then(setUser)
        .catch(() => {
          // Token is invalid, remove it
          localStorage.removeItem("auth_token")
          setToken(null)
        })
        .finally(() => setIsLoading(false))
    } else {
      setIsLoading(false)
    }
  }, [])

  const saveHistory = async (chatMessage: ChatMessage) => {
    try {
      setIsLoading(true)
      const response = await qaAPI.saveHistory(chatMessage);
    } catch (error) {
      toast({
        title: "保存失败",
        description: error instanceof Error ? error.message : "请检查网络连接",
        variant: "destructive",
      })
      throw error
    } finally {
      setIsLoading(false)
    }
  }

  const getHistory = async (conversationId: string) => {
    try {
      setIsLoading(true)
      const response = await qaAPI.getHistory(conversationId)

      return response;
    } catch (error) {
      return null
      // toast({
      //   title: "历史取得失败",
      //   description: error instanceof Error ? error.message : "历史取得过程中出现错误",
      //   variant: "destructive",
      // })
      // throw error
    } finally {
      setIsLoading(false)
    }
  }

  const logout = () => {
    setUser(null)
    setToken(null)
    localStorage.removeItem("auth_token")
    toast({
      title: "已退出登录",
      description: "感谢使用，期待您的再次访问！",
    })
  }

  const value: QaContextType = {
    saveHistory,
    getHistory,
  }

  return <QaContext.Provider value={value}>{children}</QaContext.Provider>
}

export function useQa() {
  const context = useContext(QaContext)
  if (context === undefined) {
    throw new Error("useAuth must be used within an QaProvider")
  }
  return context
}
