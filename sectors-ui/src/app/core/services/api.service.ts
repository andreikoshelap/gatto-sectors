import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Sector, SaveSectorRequest } from '../models/sector.model';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly baseUrl = 'http://localhost:8085/api';

  constructor(private http: HttpClient) {}

  // ---------- SECTORS ADMIN ----------

  getSectors(): Observable<Sector[]> {
    return this.http.get<Sector[]>(`${this.baseUrl}/sectors`);
  }

  createSector(payload: SaveSectorRequest): Observable<Sector> {
    return this.http.post<Sector>(`${this.baseUrl}/sectors`, payload);
  }

  updateSector(id: number, payload: SaveSectorRequest): Observable<Sector> {
    return this.http.put<Sector>(`${this.baseUrl}/sectors/${id}`, payload);
  }

  deleteSector(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/sectors/${id}`);
  }
}
