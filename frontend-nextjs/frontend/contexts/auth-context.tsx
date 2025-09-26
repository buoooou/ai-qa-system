"use client"

import { createContext, useContext, useEffect, useState, type ReactNode } from "react"
import type { UserProfile, LoginRequest, RegisterRequest, AuthContextType } from "@/types/auth"
import { authAPI } from "@/lib/auth-api"
import { useToast } from "@/hooks/use-toast"

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserProfile | null>(null)
  const [token, setToken] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const { toast } = useToast()

  // Load token from localStorage on mount
  useEffect(() => {
    const savedToken = localStorage.getItem("auth_token")
    const savedUser = localStorage.getItem("auth_user")
    if (savedToken && savedUser) {
      setToken(savedToken)
      setUser(JSON.parse(savedUser))
      // Try to get user info with saved token
      authAPI
        .getCurrentUser(JSON.parse(savedUser).id, savedToken)
        .then((profile) => {
          setUser(profile)
          localStorage.setItem("auth_user", JSON.stringify(profile))
        })
        .catch(() => {
          // Token is invalid, remove it
          localStorage.removeItem("auth_token")
          localStorage.removeItem("auth_user")
          setToken(null)
          setUser(null)
        })
        .finally(() => setIsLoading(false))
    } else {
      setIsLoading(false)
    }
  }, [])

  const login = async (credentials: LoginRequest) => {
    try {
      setIsLoading(true)
      const response = await authAPI.login(credentials)

      setToken(response.token)
      setUser(response.profile)
      localStorage.setItem("auth_token", response.token)
      localStorage.setItem("auth_user", JSON.stringify(response.profile))

      toast({
        title: "登录成功",
        description: `欢迎回来，${response.profile.username}！`,
      })
    } catch (error) {
      toast({
        title: "登录失败",
        description: error instanceof Error ? error.message : "请检查用户名和密码",
        variant: "destructive",
      })
      throw error
    } finally {
      setIsLoading(false)
    }
  }

  const register = async (userData: RegisterRequest) => {
    try {
      setIsLoading(true)
      const response = await authAPI.register(userData)

      setToken(response.token)
      setUser(response.profile)
      localStorage.setItem("auth_token", response.token)
      localStorage.setItem("auth_user", JSON.stringify(response.profile))

      toast({
        title: "注册成功",
        description: `欢迎加入，${response.profile.username}！`,
      })
    } catch (error) {
      toast({
        title: "注册失败",
        description: error instanceof Error ? error.message : "注册过程中出现错误",
        variant: "destructive",
      })
      throw error
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

  const value: AuthContextType = {
    user,
    token,
    login,
    register,
    logout,
    isLoading,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}
