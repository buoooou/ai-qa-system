import type {
  CoreAssistantMessage,
  CoreToolMessage,
  UIMessage,
  UIMessagePart,
} from 'ai';
import { type ClassValue, clsx } from 'clsx';
import { formatISO } from 'date-fns';
import { twMerge } from 'tailwind-merge';
import type { Document } from '@/lib/db/schema';
import type { GatewayChatMessage } from '@/lib/api/types';
import { ChatSDKError, type ErrorCode } from './errors';
import type { ChatMessage, ChatTools, CustomUIDataTypes } from './types';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export const fetcher = async (url: string) => {
  const response = await fetch(url);

  if (!response.ok) {
    // Handle 401 unauthorized - redirect to login
    if (response.status === 401) {
      if (typeof window !== 'undefined') {
        window.location.href = '/login';
      }
    }

    let errorMessage = `HTTP ${response.status}: ${response.statusText}`;

    try {
      const errorData = await response.json();
      errorMessage = errorData.message || errorData.cause || errorMessage;
    } catch (e) {
      // If we can't parse JSON, use the status text
    }

    throw new ChatSDKError(`api_error:${response.status}`, errorMessage);
  }

  return response.json();
};

export async function fetchWithErrorHandlers(
  input: RequestInfo | URL,
  init?: RequestInit,
) {
  try {
    const response = await fetch(input, init);

    if (!response.ok) {
      const { code, cause } = await response.json();
      throw new ChatSDKError(code as ErrorCode, cause);
    }

    return response;
  } catch (error: unknown) {
    if (typeof navigator !== 'undefined' && !navigator.onLine) {
      throw new ChatSDKError('offline:chat');
    }

    throw error;
  }
}

export function getLocalStorage(key: string) {
  if (typeof window !== 'undefined') {
    return JSON.parse(localStorage.getItem(key) || '[]');
  }
  return [];
}

export function generateUUID(): string {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0;
    const v = c === 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}

type ResponseMessageWithoutId = CoreToolMessage | CoreAssistantMessage;
type ResponseMessage = ResponseMessageWithoutId & { id: string };

export function getMostRecentUserMessage(messages: UIMessage[]) {
  const userMessages = messages.filter((message) => message.role === 'user');
  return userMessages.at(-1);
}

export function getDocumentTimestampByIndex(
  documents: Document[],
  index: number,
) {
  if (!documents) { return new Date(); }
  if (index > documents.length) { return new Date(); }

  return documents[index].createdAt;
}

export function getTrailingMessageId({
  messages,
}: {
  messages: ResponseMessage[];
}): string | null {
  const trailingMessage = messages.at(-1);

  if (!trailingMessage) { return null; }

  return trailingMessage.id;
}

export function sanitizeText(text: string) {
  return text.replace('<has_function_call>', '');
}

export function convertToUIMessages(messages: GatewayChatMessage[]): ChatMessage[] {
  const uiMessages: ChatMessage[] = [];

  messages.forEach((message) => {
    const baseId = String(message.id);
    const timestamp = formatISO(new Date(message.createdAt));

    // 如果有问题，创建用户消息
    if (message.question && message.question.trim()) {
      uiMessages.push({
        id: `${baseId}-question`,
        role: 'user',
        parts: [
          {
            type: 'text',
            text: message.question,
          },
        ] as UIMessagePart<CustomUIDataTypes, ChatTools>[],
        metadata: {
          createdAt: timestamp,
        },
      });
    }

    // 如果有答案，创建助手消息
    if (message.answer && message.answer.trim()) {
      uiMessages.push({
        id: `${baseId}-answer`,
        role: 'assistant',
        parts: [
          {
            type: 'text',
            text: message.answer,
          },
        ] as UIMessagePart<CustomUIDataTypes, ChatTools>[],
        metadata: {
          createdAt: timestamp,
        },
      });
    }
  });

  return uiMessages;
}

export function getTextFromMessage(message: ChatMessage): string {
  return message.parts
    .filter((part) => part.type === 'text')
    .map((part) => part.text)
    .join('');
}
