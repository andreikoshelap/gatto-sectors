export interface Sector {
  id: number;
  name: string;
  parentId?: number | null;
}

export interface SectorView extends Sector {
  level: number;
  displayName: string;
}

export interface SaveSectorRequest {
  name: string;
  parentId?: number | null;
}
