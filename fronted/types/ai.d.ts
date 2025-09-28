declare module 'ai' {
  export function useChat(options: {
    api: string;
  }): {
    messages: Array<{
      id: string;
      role: 'user' | 'assistant';
      content: string;
    }>;
    input: string;
    handleInputChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    handleSubmit: (e: React.FormEvent<HTMLFormElement>) => void;
    error?: Error;
  };
}