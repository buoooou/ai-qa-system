"use client"

import type React from "react"

import { useState, useEffect, useRef, memo } from "react"
import { Button } from "@/components/ui/button"
import { ScrollArea } from "@/components/ui/scroll-area"
import { Card, CardContent } from "@/components/ui/card"
import { Plus, MessageSquare, Trash2 } from "lucide-react"

interface User {
  id: number
  username: string
  token: string
}

interface ConversationItem {
  id: number
  userId: number
  conversationId: string
  question: string
  answer: string
  createTime: string
}

interface ConversationSidebarProps {
  user: User
  onNewConversation: () => void
  onLoadConversation: (conversationId: string) => void
  currentConversationId: string | null
  refreshTrigger?: number
}

// 在组件外部定义全局变量，确保在整个应用生命周期内只加载一次
// 即使组件重新渲染挂载，也能保持"是否已经加载过"这个状态
let globalHasLoaded = false

export const ConversationSidebar = memo(function ConversationSidebar({
  user,
  onNewConversation,
  onLoadConversation,
  currentConversationId,
  refreshTrigger,
}: ConversationSidebarProps) {
  const [conversations, setConversations] = useState<ConversationItem[]>([])
  const [isLoading, setIsLoading] = useState(false)

  const loadConversations = async () => {
    if (!user.token) return

    setIsLoading(true)
    try {
      const response = await fetch("http://3.26.56.14:8080/api/qa/conversations/allHistory", {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${user.token}`,
        },
      })

      const data = await response.json()

      if (data.code === 200) {
        // Group conversations by conversationId and take the first message as title
        const grouped = data.data.reduce((acc: any, item: ConversationItem) => {
          if (!acc[item.conversationId]) {
            acc[item.conversationId] = item
          }
          return acc
        }, {})

        setConversations(Object.values(grouped))
      }
    } catch (error) {
      console.error("Failed to load conversations:", error)
    } finally {
      setIsLoading(false)
    }
  }

  const deleteConversation = async (conversationId: string, e: React.MouseEvent) => {
    e.stopPropagation()

    try {
      const response = await fetch(
        `http://3.26.56.14:8080/api/qa/conversation/delete?conversationId=${conversationId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${user.token}`,
          },
        },
      )

      const data = await response.json()

      if (data.code === 200) {
        setConversations((prev) => prev.filter((conv) => conv.conversationId !== conversationId))
        if (currentConversationId === conversationId) {
          onNewConversation()
        }
      }
    } catch (error) {
      console.error("Failed to delete conversation:", error)
    }
  }

  useEffect(() => {
    // 只在首次加载或 refreshTrigger 变化时加载对话历史
    console.log("useEffect globalHasLoaded: ", globalHasLoaded)
    console.log("useEffect refreshTrigger: ", refreshTrigger)

    if (!globalHasLoaded || (refreshTrigger && refreshTrigger > 0) || (globalHasLoaded && refreshTrigger === 0)) {
      globalHasLoaded = true
      loadConversations()
    }
  }, [refreshTrigger])

  // useEffect(() => {
  //   if (!hasLoadedRef.current) {
  //     hasLoadedRef.current = true
  //     loadConversations()
  //   }
  // }, [])

  // useEffect(() => {
  //   if (refreshTrigger && refreshTrigger > 0) {
  //     loadConversations()
  //   }
  // }, [refreshTrigger])

  const formatTitle = (question: string) => {
    return question.length > 25 ? question.substring(0, 25) + "..." : question
  }

  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    const now = new Date()
    const diffTime = Math.abs(now.getTime() - date.getTime())
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

    if (diffDays === 1) return "Today"
    if (diffDays === 2) return "Yesterday"
    if (diffDays <= 7) return `${diffDays - 1} days ago`
    return date.toLocaleDateString()
  }

  return (
    <div className="w-80 border-r border-border bg-muted/30 flex flex-col overflow-hidden">
      {/* Header */}
      <div className="p-4 border-b border-border flex-shrink-0">
        <h2 className="font-semibold text-foreground mb-3">AI Chat Assistant</h2>
        <Button onClick={onNewConversation} className="w-full flex items-center gap-2">
          <Plus className="h-4 w-4" />
          New Conversation
        </Button>
      </div>

      {/* Conversations List */}
      <div className="flex-1 p-4 overflow-hidden">
        <h3 className="text-sm font-medium text-muted-foreground mb-3">Conversation History</h3>
        <div className="h-full overflow-hidden">
          <ScrollArea className="h-full">
            {isLoading ? (
              <div className="flex items-center justify-center py-8">
                <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-primary"></div>
              </div>
            ) : conversations.length === 0 ? (
              <div className="text-center py-8 text-muted-foreground">
                <MessageSquare className="h-8 w-8 mx-auto mb-2 opacity-50" />
                <p className="text-sm">No conversations yet</p>
              </div>
            ) : (
              <div className="space-y-2">
                {conversations.map((conversation) => (
                  <Card
                    key={conversation.conversationId}
                    className={`cursor-pointer transition-colors hover:bg-accent group ${currentConversationId === conversation.conversationId ? "bg-accent border-primary" : ""
                      }`}
                    onClick={() => onLoadConversation(conversation.conversationId)}
                  >
                    <CardContent className="p-3">
                      <div className="flex items-start justify-between">
                        <div className="flex-1 min-w-0">
                          <p className="text-sm font-medium text-foreground truncate">
                            {formatTitle(conversation.question)}
                          </p>
                          <p className="text-xs text-muted-foreground mt-1">{formatDate(conversation.createTime)}</p>
                        </div>
                        <Button
                          variant="ghost"
                          size="sm"
                          className="opacity-0 group-hover:opacity-100 transition-opacity ml-2 h-6 w-6 p-0"
                          onClick={(e) => deleteConversation(conversation.conversationId, e)}
                        >
                          <Trash2 className="h-3 w-3" />
                        </Button>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </ScrollArea>
        </div>
      </div>
    </div>
  )
})
