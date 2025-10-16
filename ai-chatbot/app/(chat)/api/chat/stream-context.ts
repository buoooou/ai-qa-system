export type StreamContext = {
  latestStreamId: string | null;
  resumableStream: (streamId: string, fallback: () => ReadableStream) => Promise<ReadableStream | null>;
} | null;

export function getStreamContext(): StreamContext {
  // TODO: Implement proper stream context for resumable streams
  return null;
}
