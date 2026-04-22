import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Disponibilite } from '../models/disponibilite.model';

export interface ResultatGeneration {
    nombrePlacements: number;
    nombreCoursNonPlaces: number;
    placements: PlacementEffectue[];
    coursNonPlaces: CoursNonPlace[];
}

export interface PlacementEffectue {
    coursId: number;
    nomCours: string;
    enseignantId: number;
    classeId: number;
    salleId: number;
    nomSalle: string;
    jour: string;
    heureDebut: string;
    heureFin: string;
    score: number;
}

export interface CoursNonPlace {
    coursId: number;
    nomCours: string;
    enseignantId: number;
    volumeRestant: number;
    raison: string;
}

@Injectable({ providedIn: 'root' })
export class PlanningService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/disponibilites`;
    private generationUrl = `${environment.apiUrl}/generation`;

    getEnseignantDisponibilites(enseignantId: number): Observable<Disponibilite[]> {
        return this.http.get<Disponibilite[]>(`${this.apiUrl}/enseignant/${enseignantId}`);
    }

    createDisponibilite(dispo: Disponibilite): Observable<Disponibilite> {
        return this.http.post<Disponibilite>(this.apiUrl, dispo);
    }

    updateDisponibilite(id: number, dispo: Disponibilite): Observable<Disponibilite> {
        return this.http.put<Disponibilite>(`${this.apiUrl}/${id}`, dispo);
    }

    deleteDisponibilite(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    deleteEnseignantDisponibilites(enseignantId: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/enseignant/${enseignantId}`);
    }

    /**
     * Déclenche la génération automatique de l'emploi du temps pour toutes les classes.
     * Aucun paramètre de semaine n'est envoyé au backend.
     */
    genererEmploiDuTemps(): Observable<ResultatGeneration> {
        return this.http.post<ResultatGeneration>(this.generationUrl, null);
    }

    /**
     * Récupère les emplois du temps persistés en base.
     * @param classeId filtre optionnel par classe
     */
    getEmploiDuTemps(classeId?: number): Observable<PlacementEffectue[]> {
        const params = classeId ? `?classeId=${classeId}` : '';
        return this.http.get<PlacementEffectue[]>(`${this.generationUrl}${params}`);
    }
}
