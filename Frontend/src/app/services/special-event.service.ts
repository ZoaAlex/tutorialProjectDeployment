import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DemandeEvent, StatutDemande } from '../models/demande-event.model';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class SpecialEventService {
    private apiUrl = `${environment.apiUrl}/demandes`;

    constructor(private http: HttpClient) { }

    getAllDemandes(): Observable<DemandeEvent[]> {
        return this.http.get<DemandeEvent[]>(this.apiUrl);
    }

    getDemandeById(id: number): Observable<DemandeEvent> {
        return this.http.get<DemandeEvent>(`${this.apiUrl}/${id}`);
    }

    getDemandesByEnseignant(enseignantId: number): Observable<DemandeEvent[]> {
        // Based on backend, we might need a specific endpoint or filter client side 
        // For now, let's assume we filter client side or backend gets them all and we filter
        return this.http.get<DemandeEvent[]>(this.apiUrl);
    }

    createDemande(demande: DemandeEvent): Observable<DemandeEvent> {
        return this.http.post<DemandeEvent>(this.apiUrl, demande);
    }

    updateDemande(id: number, demande: DemandeEvent): Observable<DemandeEvent> {
        // Backend doesn't have a full PUT/PATCH for all fields yet, but we'll use the same URL if needed
        // or just assume PATCH for status for now. 
        return this.http.patch<DemandeEvent>(`${this.apiUrl}/${id}`, demande);
    }

    deleteDemande(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
    validateDemande(id: number): Observable<DemandeEvent> {
        return this.http.patch<DemandeEvent>(`${this.apiUrl}/${id}/status?status=VALIDE`, {});
    }
    refuseDemande(id: number): Observable<DemandeEvent> {
        return this.http.patch<DemandeEvent>(`${this.apiUrl}/${id}/status?status=REFUSE`, {});
    }

    updateStatus(id: number, status: StatutDemande): Observable<DemandeEvent> {
        return this.http.patch<DemandeEvent>(`${this.apiUrl}/${id}/status?status=${status}`, {});
    }
}
