// Stub artifact definitions for compatibility
export type ArtifactKind = "text" | "code" | "image" | "sheet";

export type UIArtifact = {
  kind: ArtifactKind;
  id: string;
  title: string;
  text: string;
};

export const artifactDefinitions = [];

// Stub component
export function Artifact() {
  return null;
}
