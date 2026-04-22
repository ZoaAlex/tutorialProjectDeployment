import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Classe } from '../models/classe.model';
import { Cours } from '../models/cours.model';
import { Etudiant } from '../models/etudiant.model';
import { Filiere } from '../models/filiere.model';
import { Ue } from '../models/ue.model';
import { Ecole } from '../models/ecole.model';
import { GroupeEtudiant } from '../models/groupe-etudiant.model';

export type ImportEntityType = 'classes' | 'filieres' | 'ue' | 'etudiant' | 'groupe' | 'ecoles' | 'cours';

@Injectable({
    providedIn: 'root'
})
export class CoursClasseService {
    constructor(private http: HttpClient) { }

    // Ecoles
    getAllEcoles(): Observable<Ecole[]> {
        return this.http.get<Ecole[]>(`${environment.apiUrl}/ecoles`);
    }
    createEcole(ecole: Ecole): Observable<Ecole> {
        return this.http.post<Ecole>(`${environment.apiUrl}/ecoles`, ecole);
    }
    updateEcole(id: number, ecole: Ecole): Observable<Ecole> {
        return this.http.put<Ecole>(`${environment.apiUrl}/ecoles/${id}`, ecole);
    }
    deleteEcole(id: number): Observable<void> {
        return this.http.delete<void>(`${environment.apiUrl}/ecoles/${id}`);
    }

    // Filieres
    getAllFilieres(): Observable<Filiere[]> {
        return this.http.get<Filiere[]>(`${environment.apiUrl}/filieres`);
    }
    createFiliere(filiere: Filiere): Observable<Filiere> {
        return this.http.post<Filiere>(`${environment.apiUrl}/filieres`, filiere);
    }
    updateFiliere(id: number, filiere: Filiere): Observable<Filiere> {
        return this.http.put<Filiere>(`${environment.apiUrl}/filieres/${id}`, filiere);
    }
    deleteFiliere(id: number): Observable<void> {
        return this.http.delete<void>(`${environment.apiUrl}/filieres/${id}`);
    }

    // UEs
    getAllUes(): Observable<Ue[]> {
        return this.http.get<Ue[]>(`${environment.apiUrl}/ues`);
    }
    createUe(ue: Ue): Observable<Ue> {
        console.log(ue);
        return this.http.post<Ue>(`${environment.apiUrl}/ues`, ue);
    }
    updateUe(id: number, ue: Ue): Observable<Ue> {
        return this.http.put<Ue>(`${environment.apiUrl}/ues/${id}`, ue);
    }
    deleteUe(id: number): Observable<void> {
        return this.http.delete<void>(`${environment.apiUrl}/ues/${id}`);
    }

    getUE(id: number) {
        return this.http.get<Ue[]>(`${environment.apiUrl}/ues/${id}`);
    }

    // Classes
    getAllClasses(): Observable<Classe[]> {
        return this.http.get<Classe[]>(`${environment.apiUrl}/classes`);
    }
    createClasse(classe: Classe): Observable<Classe> {
        return this.http.post<Classe>(`${environment.apiUrl}/classes`, classe);
    }
    updateClasse(id: number, classe: Classe): Observable<Classe> {
        return this.http.put<Classe>(`${environment.apiUrl}/classes/${id}`, classe);
    }
    deleteClasse(id: number): Observable<void> {
        return this.http.delete<void>(`${environment.apiUrl}/classes/${id}`);
    }

    // Cours
    getAllCours(): Observable<Cours[]> {
        return this.http.get<Cours[]>(`${environment.apiUrl}/cours`);
    }
    createCours(cours: Cours): Observable<Cours> {
        return this.http.post<Cours>(`${environment.apiUrl}/cours`, cours);
    }
    updateCours(id: number, cours: Cours): Observable<Cours> {
        // Le backend expose PUT /cours/update (sans id dans l'URL)
        return this.http.put<Cours>(`${environment.apiUrl}/cours/update`, cours);
    }
    deleteCours(id: number): Observable<void> {
        return this.http.delete<void>(`${environment.apiUrl}/cours/${id}`);
    }

    // Etudiants
    getAllEtudiants(): Observable<Etudiant[]> {
        return this.http.get<Etudiant[]>(`${environment.apiUrl}/etudiants`);
    }
    createEtudiant(etudiant: Etudiant): Observable<Etudiant> {
        return this.http.post<Etudiant>(`${environment.apiUrl}/etudiants`, etudiant);
    }
    updateEtudiant(id: number, etudiant: Etudiant): Observable<Etudiant> {
        return this.http.put<Etudiant>(`${environment.apiUrl}/etudiants/${id}`, etudiant);
    }
    deleteEtudiant(id: number): Observable<void> {
        return this.http.delete<void>(`${environment.apiUrl}/etudiants/${id}`);
    }

    // Groupes
    getAllGroupes(): Observable<GroupeEtudiant[]> {
        return this.http.get<GroupeEtudiant[]>(`${environment.apiUrl}/groupes-etudiants`);
    }
    createGroupe(groupe: GroupeEtudiant): Observable<GroupeEtudiant> {
        return this.http.post<GroupeEtudiant>(`${environment.apiUrl}/groupes-etudiants`, groupe);
    }
    updateGroupe(id: number, groupe: GroupeEtudiant): Observable<GroupeEtudiant> {
        return this.http.put<GroupeEtudiant>(`${environment.apiUrl}/groupes-etudiants/${id}`, groupe);
    }
    deleteGroupe(id: number): Observable<void> {
        return this.http.delete<void>(`${environment.apiUrl}/groupes-etudiants/${id}`);
    }

    // Import Excel
    importerExcel(entityType: ImportEntityType, file: File): Observable<string> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post(`${environment.apiUrl}/import/${entityType}`, formData, { responseType: 'text' });
    }
}
