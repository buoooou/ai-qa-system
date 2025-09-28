"use client";

import { createContext, useContext, type ReactNode } from "react";

type DataStreamProviderType = {
  dataStream: unknown[] | undefined;
  setDataStream: (stream: unknown[] | undefined) => void;
};

const DataStreamContext = createContext<DataStreamProviderType | undefined>(
  undefined
);

export function useDataStream() {
  const context = useContext(DataStreamContext);
  if (!context) {
    throw new Error("useDataStream must be used within a DataStreamProvider");
  }
  return context;
}

export function DataStreamProvider({ children }: { children: ReactNode }) {
  // Minimal stub implementation
  const dataStream = undefined;
  const setDataStream = () => {
    // No-op for now since artifacts are disabled
  };

  return (
    <DataStreamContext.Provider value={{ dataStream, setDataStream }}>
      {children}
    </DataStreamContext.Provider>
  );
}
