// Stub artifact hook for compatibility
export const initialArtifactData = {
  kind: "text" as const,
  id: "",
  title: "",
  text: "",
  isVisible: false,
};

export function useArtifact() {
  return {
    artifact: initialArtifactData,
    setArtifact: () => {},
    setMetadata: () => {},
  };
}

export function useArtifactSelector(selector: (state: typeof initialArtifactData) => any) {
  return selector(initialArtifactData);
}