"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { X } from "lucide-react"
import { useIsMobile } from "@/hooks/use-mobile"
import { Sheet, SheetContent, SheetHeader, SheetTitle } from "@/components/ui/sheet"

interface UserSettingsPanelProps {
  user: { id: number; username: string; token: string }
  onClose: () => void
  onLogout: () => void
}

export function UserSettingsPanel({ user, onClose, onLogout }: UserSettingsPanelProps) {
  const [selectedModel, setSelectedModel] = useState("google-gemini")
  const isMobile = useIsMobile()

  const SettingsContent = () => (
    <div className="space-y-6">
      {/* User Info */}
      <Card>
        <CardHeader className="pb-3">
          <CardTitle className="text-sm flex items-center gap-2">
            {/* User Icon */}
            <span className="h-4 w-4" style={{ marginRight: "4px" }}>
              User
            </span>
            User Information
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          <div>
            <Label className="text-xs text-muted-foreground">Username</Label>
            <p className="text-sm font-medium text-foreground">{user.username}</p>
          </div>
          <div>
            <Label className="text-xs text-muted-foreground">User ID</Label>
            <p className="text-sm font-medium text-foreground">{user.id}</p>
          </div>
        </CardContent>
      </Card>

      {/* AI Model Selection */}
      <Card>
        <CardHeader className="pb-3">
          <CardTitle className="text-sm flex items-center gap-2">
            {/* Bot Icon */}
            <span className="h-4 w-4" style={{ marginRight: "4px" }}>
              Bot
            </span>
            AI Model
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-2">
            <Label className="text-xs text-muted-foreground">Select Model</Label>
            <Select value={selectedModel} onValueChange={setSelectedModel}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="google-gemini">Google Gemini</SelectItem>
              </SelectContent>
            </Select>
            <p className="text-xs text-muted-foreground">Currently using Google's Gemini model for AI responses.</p>
          </div>
        </CardContent>
      </Card>

      {/* Actions */}
      <div className="space-y-2">
        <Button variant="destructive" className="w-full flex items-center gap-2" onClick={onLogout}>
          {/* LogOut Icon */}
          <span className="h-4 w-4" style={{ marginRight: "4px" }}>
            LogOut
          </span>
          Sign Out
        </Button>
      </div>
    </div>
  )

  if (isMobile) {
    return (
      <Sheet open={true} onOpenChange={onClose}>
        <SheetContent side="right" className="w-full sm:w-80">
          <SheetHeader>
            <SheetTitle>Settings</SheetTitle>
          </SheetHeader>
          <div className="mt-6">
            <SettingsContent />
          </div>
        </SheetContent>
      </Sheet>
    )
  }

  // Desktop layout
  return (
    <div className="w-80 border-l border-border bg-background flex flex-col">
      {/* Header */}
      <div className="p-4 border-b border-border flex items-center justify-between">
        <h3 className="font-semibold text-foreground">Settings</h3>
        <Button variant="ghost" size="sm" onClick={onClose}>
          <X className="h-4 w-4" />
        </Button>
      </div>

      {/* Content */}
      <div className="flex-1 p-4">
        <SettingsContent />
      </div>
    </div>
  )
}
