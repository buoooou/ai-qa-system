"use client"

import { useState, useCallback, useEffect } from "react"
import { Sidebar } from "@/components/sidebar"
import { ChatWindow } from "@/components/chat-window"
import { ProtectedRoute } from "@/components/auth/protected-route"
import type { Conversation } from "@/types/chat"

function HomePage() {
  const [conversations, setConversations] = useState<Conversation[]>([])
  const [activeConversationId, setActiveConversationId] = useState("")
  const [newConversationId, setNewConversationId] = useState<string | undefined>() // 跟踪最新创建的对话
  const [isLoading, setIsLoading] = useState(false) // 新建对话的加载状态
  const [conversationMessages, setConversationMessages] = useState<Record<string, Conversation["messages"]>>({}) // 存储各对话的消息

  const generateConversationTitle = (firstMessage: string): string => {
    const title = firstMessage.length > 30 ? firstMessage.substring(0, 30) + "..." : firstMessage
    return title || "新对话"
  }

   useEffect(() => {
    // 只在首次加载且没有会话时创建
    setIsLoading(true)
    try {
      setConversations([]);
      handleNewChat();
    } finally {
      setIsLoading(false)
    }
  }, [])

  const handleNewChat = useCallback(() => {
    setIsLoading(true)
    try {
    const newConversation: Conversation = {
      id: crypto.randomUUID(),
      title: "新对话",
      messages: [],
      createdAt: new Date(),
      updatedAt: new Date(),
    }

    // 添加新对话到列表
    setConversations((prev) => [newConversation, ...prev])
    // 设置新对话ID用于高亮
    setNewConversationId(newConversation.id)
    // 清空当前活跃对话，显示初始页面
    setActiveConversationId(newConversation.id)
    // 清空消息存储
    setConversationMessages(prev => ({ ...prev, [newConversation.id]: [] }))
  } finally {
    setIsLoading(false)
  }
  }, [])

  const handleSelectConversation = useCallback((conversationId: string, messages?: Conversation["messages"]) => {
    
    setActiveConversationId(conversationId)
    // 清除新建对话的高亮状态
    setNewConversationId(undefined)
    // 设置当前新对话ID（如果是新对话的话）
    setNewConversationId(conversationId)
    // 存储加载的消息
    // setConversationMessages(prev => ({ ...prev, [conversationId]: messages || [] }))
  }, [])

  const handleDeleteConversation = useCallback(
    (conversationId: string) => {
      setConversations((prev) => prev.filter((conv) => conv.id !== conversationId))
        // 删除消息存储
        setConversationMessages(prev => {
          const newMessages = { ...prev }
          delete newMessages[conversationId]
          return newMessages
        })
      
        if (activeConversationId === conversationId) {
          setActiveConversationId("")
        }
      },
      [activeConversationId],
    )

  const handleRenameConversation = useCallback((conversationId: string, newTitle: string) => {
    setConversations((prev) =>
      prev.map((conv) => (conv.id === conversationId ? { ...conv, title: newTitle, updatedAt: new Date() } : conv)),
    )
  }, [])

  const handleMessageAdded = useCallback(
    (message: { role: "user" | "assistant"; content: string }) => {
      if (!activeConversationId) return

      const newMessage = {
        id: crypto.randomUUID(),
        role: message.role,
        content: message.content,
        timestamp: new Date(),
      }

      // 更新对话列表中的消息
      setConversations((prev) =>
        prev.map((conv) =>
          conv.id === activeConversationId
            ? {
                ...conv,
                messages: [...conv.messages, newMessage],
                updatedAt: new Date(),
              }
            : conv,
        ),
      )

      // 更新消息存储
      setConversationMessages(prev => ({
        ...prev,
        [activeConversationId]: [...(prev[activeConversationId] || []), newMessage]
      }))
    },
    [activeConversationId],
  )

  const handleFirstMessage = useCallback(
    (content: string) => {
      if (!newConversationId) return

      const title = generateConversationTitle(content)
      // 更新新对话的标题
      setConversations((prev) =>
        prev.map((conv) =>
          conv.id === newConversationId
            ? {
                ...conv,
                title,
                updatedAt: new Date(),
              }
            : conv,
        ),
      )
      
      // 将新对话设为活跃状态
      setActiveConversationId(newConversationId)
    },
    [newConversationId],
  )

  const activeConversation = conversations.find((conv) => conv.id === activeConversationId)
  // 获取当前对话的消息
  const currentMessages = activeConversationId 
    ? conversationMessages[activeConversationId] || [] 
    : []

  return (
    <div className="flex h-screen bg-background">
      <Sidebar
        conversations={conversations}
        activeConversationId={activeConversationId}
        newConversationId={newConversationId}
        onNewChat={handleNewChat}
        onSelectConversation={handleSelectConversation}
        onDeleteConversation={handleDeleteConversation}
        onRenameConversation={handleRenameConversation}
        isLoading={isLoading}
      />

      <div className="flex-1 flex flex-col">
        <ChatWindow
          conversationId={activeConversationId}
          conversationTitle={activeConversation?.title}
          initialMessages={activeConversation?.messages || []}
          onMessageAdded={handleMessageAdded}
          onFirstMessage={handleFirstMessage}
        />
      </div>
    </div>
  )
}

export default function ProtectedHomePage() {
  return (
    <ProtectedRoute>
      <HomePage />
    </ProtectedRoute>
  )
}
