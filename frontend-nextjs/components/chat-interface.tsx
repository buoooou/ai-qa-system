"use client"

import type React from "react"

// add callback to avoid unnecessary re-renders
// import { useState, useEffect, useRef } from "react"
import { useState, useEffect, useRef, useCallback } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { ScrollArea } from "@/components/ui/scroll-area"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { Card, CardContent } from "@/components/ui/card"
import { ConversationSidebar } from "@/components/conversation-sidebar"
import { UserSettingsPanel } from "@/components/user-settings-panel"
import { Send, Settings, MessageSquare, Menu } from "lucide-react"
import { useIsMobile } from "@/hooks/use-mobile"
import { Sheet, SheetContent } from "@/components/ui/sheet"

interface User {
  id: number
  username: string
  token: string
}

interface Message {
  id: number
  userId: number
  conversationId: string
  question: string
  answer: string
  createTime: string
}

interface ChatInterfaceProps {
  user: User
  onLogout: () => void
}

export function ChatInterface({ user, onLogout }: ChatInterfaceProps) {
  const [messages, setMessages] = useState<Message[]>([])
  const [currentInput, setCurrentInput] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [currentConversationId, setCurrentConversationId] = useState<string | null>(null)
  const [showSettings, setShowSettings] = useState(false)
  const [conversationTitle, setConversationTitle] = useState("New Conversation")
  const [refreshTrigger, setRefreshTrigger] = useState(0)
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const isMobile = useIsMobile()
  const [showSidebar, setShowSidebar] = useState(false)

  // 使用 useRef 来跟踪当前是否是新对话
  const isNewConversationRef = useRef(false)
  // 使用 useRef 来跟踪是否应该刷新对话历史
  // const shouldRefreshRef = useRef(false)

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const sendMessage = async () => {
    if (!currentInput.trim() || isLoading) return

    const question = currentInput.trim()
    const wasNewConversation = !currentConversationId
    setCurrentInput("")
    setIsLoading(true)

    try {
      const response = await fetch("http://localhost:8080/api/qa/question", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${user.token}`,
        },
        body: JSON.stringify({
          conversationId: currentConversationId,
          question: question,
        }),
      })

      const data = await response.json()

      if (data.code === 200) {
        const newMessage = data.data
        setMessages((prev) => [...prev, newMessage])

        if (!currentConversationId) {
          setCurrentConversationId(newMessage.conversationId)
          setConversationTitle(question.length > 30 ? question.substring(0, 30) + "..." : question)

          // 如果是新对话并且发送了第一条消息，设置刷新标志
          if (isNewConversationRef.current) {
            // shouldRefreshRef.current = true
            setRefreshTrigger((prev) => prev + 1)
            isNewConversationRef.current = false // 重置标志
          }

        } else {
          if (currentConversationId !== newMessage.conversationId) {
            setCurrentConversationId(newMessage.conversationId)
          }
        }
      } else {
        console.error("Failed to send message:", data.message)
      }
    } catch (error) {
      console.error("Network error:", error)
    } finally {
      setIsLoading(false)
    }
  }

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault()
      sendMessage()
    }
  }

  // add callback to avoid unnecessary re-renders
  // const startNewConversation = () => {
  const startNewConversation = useCallback(() => {
    setMessages([])
    setCurrentConversationId(null)
    setConversationTitle("New Conversation")
    isNewConversationRef.current = true // 标记为新对话

    // 如果是新对话，并且有新问题的话，刷新对话历史
    // if (shouldRefreshRef.current) {
    //   setRefreshTrigger((prev) => prev + 1)
    //   shouldRefreshRef.current = false // 重置标志
    // }

    if (isMobile) {
      setShowSidebar(false)
    }
    // }
  }, [isMobile])

  // add callback to avoid unnecessary re-renders
  // const loadConversation = async (conversationId: string) => {
  const loadConversation = useCallback(async (conversationId: string) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/qa/conversation/history?conversationId=${conversationId}`,
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${user.token}`,
          },
        },
      )

      const data = await response.json()

      if (data.code === 200) {
        setMessages(data.data)
        setCurrentConversationId(conversationId)
        if (data.data.length > 0) {
          const firstQuestion = data.data[0].question
          setConversationTitle(firstQuestion.length > 30 ? firstQuestion.substring(0, 30) + "..." : firstQuestion)
        }
        if (isMobile) {
          setShowSidebar(false)
        }
      } else {
        console.error("Failed to load conversation:", data.message)
      }
    } catch (error) {
      console.error("Failed to load conversation:", error)
    }
    // }
  }, [user.token, isMobile])

  // add callback to avoid unnecessary re-renders
  // const SidebarComponent = () => (
  const SidebarComponent = useCallback(() => (
    <ConversationSidebar
      user={user}
      onNewConversation={startNewConversation}
      onLoadConversation={loadConversation}
      currentConversationId={currentConversationId}
      refreshTrigger={refreshTrigger}
    />
    // )
  ), [user, startNewConversation, loadConversation, currentConversationId, refreshTrigger])

  return (
    <div className="flex h-screen bg-background overflow-hidden">
      {!isMobile && <SidebarComponent />}

      {isMobile && (
        <Sheet open={showSidebar} onOpenChange={setShowSidebar}>
          <SheetContent side="left" className="w-80 p-0">
            <SidebarComponent />
          </SheetContent>
        </Sheet>
      )}

      <div className="flex-1 flex flex-col overflow-hidden">
        <div className="border-b border-border p-4 flex items-center justify-between flex-shrink-0">
          <div className="flex items-center gap-3">
            {isMobile && (
              <Button variant="ghost" size="sm" onClick={() => setShowSidebar(true)} className="p-2">
                <Menu className="h-5 w-5" />
              </Button>
            )}
            <MessageSquare className="h-5 w-5 text-muted-foreground" />
            <h1 className="font-semibold text-foreground truncate">{conversationTitle}</h1>
          </div>
          <Button
            variant="ghost"
            size="sm"
            onClick={() => setShowSettings(!showSettings)}
            className="flex items-center gap-2"
          >
            <Avatar className="h-6 w-6">
              <AvatarFallback className="text-xs">{user.username.charAt(0).toUpperCase()}</AvatarFallback>
            </Avatar>
            <Settings className="h-4 w-4" />
          </Button>
        </div>

        <div className="flex-1 overflow-hidden">
          <ScrollArea className="h-full">
            <div className="p-4">
              <div className="space-y-4 max-w-4xl mx-auto">
                {messages.length === 0 ? (
                  <div className="text-center text-muted-foreground py-12">
                    <MessageSquare className="h-12 w-12 mx-auto mb-4 opacity-50" />
                    <p className="text-lg font-medium mb-2">Start a new conversation</p>
                    <p className="text-sm">Ask me anything and I'll help you with AI-powered responses.</p>
                  </div>
                ) : (
                  messages.map((message) => (
                    <div key={message.id} className="space-y-4">
                      <div className="flex justify-end">
                        <Card
                          className={`${isMobile ? "max-w-[85%]" : "max-w-[80%]"} bg-primary text-primary-foreground`}
                        >
                          <CardContent className="p-3">
                            <p className="text-sm">{message.question}</p>
                          </CardContent>
                        </Card>
                      </div>

                      <div className="flex justify-start">
                        <Card className={`${isMobile ? "max-w-[85%]" : "max-w-[80%]"} bg-muted`}>
                          <CardContent className="p-3">
                            <p className="text-sm text-muted-foreground whitespace-pre-wrap">{message.answer}</p>
                          </CardContent>
                        </Card>
                      </div>
                    </div>
                  ))
                )}

                {isLoading && (
                  <div className="flex justify-start">
                    <Card className={`${isMobile ? "max-w-[85%]" : "max-w-[80%]"} bg-muted`}>
                      <CardContent className="p-3">
                        <div className="flex items-center gap-2">
                          <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-primary"></div>
                          <p className="text-sm text-muted-foreground">AI is thinking...</p>
                        </div>
                      </CardContent>
                    </Card>
                  </div>
                )}
                <div ref={messagesEndRef} />
              </div>
            </div>
          </ScrollArea>
        </div>

        <div className="border-t border-border p-4 flex-shrink-0">
          <div className={`${isMobile ? "px-2" : "max-w-4xl mx-auto"} flex gap-2`}>
            <Input
              value={currentInput}
              onChange={(e) => setCurrentInput(e.target.value)}
              onKeyPress={handleKeyPress}
              placeholder="Type your message here..."
              disabled={isLoading}
              className="flex-1"
            />
            <Button onClick={sendMessage} disabled={isLoading || !currentInput.trim()}>
              <Send className="h-4 w-4" />
            </Button>
          </div>
        </div>
      </div>

      {showSettings && <UserSettingsPanel user={user} onClose={() => setShowSettings(false)} onLogout={onLogout} />}
    </div>
  )
}
