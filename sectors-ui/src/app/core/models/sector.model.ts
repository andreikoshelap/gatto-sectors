export interface Sector {
  id: number;
  name: string;
  parentId?: number | null;
}

export interface SaveSectorRequest {
  name: string;
  parentId?: number | null;
}
