import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Salle, CreateSalleRequest, SalleSearchCriteria, StatistiquesSalles, StatutSalle } from '../models/salle.model';
import { Materiel, CreateMaterielRequest } from '../models/materiel.model';
import { Reservation } from '../models/reservation.model';

@Injectable({ providedIn: 'root' })
export class SalleService {
    private sallesUrl  = `${environment.apiUrl}/salles`;
    private materielsUrl = `${environment.apiUrl}/materiels`;
    private reservationsUrl = `${environment.apiUrl}/reservations`;

    constructor(private http: HttpClient) {}

    // ─── SALLES ────────────────────────────────────────────────

    getAllSalles(): Observable<Salle[]> {
        return this.http.get<Salle[]>(this.sallesUrl);
    }

    getSalleById(id: number): Observable<Salle> {
        return this.http.get<Salle>(`${this.sallesUrl}/${id}`);
    }

    getSalleByCode(codeSalle: string): Observable<Salle> {
        return this.http.get<Salle>(`${this.sallesUrl}/code/${codeSalle}`);
    }

    getSallesParEcole(ecoleId: number): Observable<Salle[]> {
        return this.http.get<Salle[]>(`${this.sallesUrl}/ecole/${ecoleId}`);
    }

    /** GET /api/salles/disponibles — dateDebut et dateFin obligatoires */
    getSallesDisponibles(dateDebut: string, dateFin: string, ecoleId?: number): Observable<Salle[]> {
        let params = new HttpParams()
            .set('dateDebut', dateDebut)
            .set('dateFin', dateFin);
        if (ecoleId) params = params.set('ecoleId', ecoleId.toString());
        return this.http.get<Salle[]>(`${this.sallesUrl}/disponibles`, { params });
    }

    /** GET /api/salles/disponibles/capacite — dateDebut et dateFin obligatoires */
    getSallesAvecCapaciteDisponibles(capaciteRequise: number, dateDebut: string, dateFin: string): Observable<Salle[]> {
        const params = new HttpParams()
            .set('capaciteRequise', capaciteRequise.toString())
            .set('dateDebut', dateDebut)
            .set('dateFin', dateFin);
        return this.http.get<Salle[]>(`${this.sallesUrl}/disponibles/capacite`, { params });
    }

    /** POST /api/salles/recherche — critères multiples */
    rechercherSalles(criteria: SalleSearchCriteria): Observable<Salle[]> {
        return this.http.post<Salle[]>(`${this.sallesUrl}/recherche`, criteria);
    }

    creerSalle(request: CreateSalleRequest): Observable<Salle> {
        return this.http.post<Salle>(this.sallesUrl, request);
    }

    updateSalle(id: number, salle: Salle): Observable<Salle> {
        return this.http.put<Salle>(`${this.sallesUrl}/${id}`, salle);
    }

    changerStatutSalle(id: number, statut: StatutSalle): Observable<Salle> {
        return this.http.patch<Salle>(`${this.sallesUrl}/${id}/statut`, null, {
            params: new HttpParams().set('statut', statut)
        });
    }

    deleteSalle(id: number): Observable<void> {
        return this.http.delete<void>(`${this.sallesUrl}/${id}`);
    }

    salleExiste(id: number): Observable<boolean> {
        return this.http.get<boolean>(`${this.sallesUrl}/${id}/existe`);
    }

    codeSalleExiste(codeSalle: string): Observable<boolean> {
        return this.http.get<boolean>(`${this.sallesUrl}/code/${codeSalle}/existe`);
    }

    getStatistiques(): Observable<StatistiquesSalles> {
        return this.http.get<StatistiquesSalles>(`${this.sallesUrl}/statistiques`);
    }

    // ─── MATÉRIELS ─────────────────────────────────────────────

    getAllMateriel(): Observable<Materiel[]> {
        return this.http.get<Materiel[]>(this.materielsUrl);
    }

    getMaterielById(id: number): Observable<Materiel> {
        return this.http.get<Materiel>(`${this.materielsUrl}/${id}`);
    }

    /** GET /api/materiels/salle/{salleId} */
    getMaterielParSalle(salleId: number): Observable<Materiel[]> {
        return this.http.get<Materiel[]>(`${this.materielsUrl}/salle/${salleId}`);
    }

    getMaterielFonctionnelParSalle(salleId: number): Observable<Materiel[]> {
        return this.http.get<Materiel[]>(`${this.materielsUrl}/salle/${salleId}/fonctionnel`);
    }

    getMaterielParType(type: string): Observable<Materiel[]> {
        return this.http.get<Materiel[]>(`${this.materielsUrl}/type/${type}`);
    }

    getMaterielParEtat(etat: string): Observable<Materiel[]> {
        return this.http.get<Materiel[]>(`${this.materielsUrl}/etat/${etat}`);
    }

    getMaterielEnMaintenance(): Observable<Materiel[]> {
        return this.http.get<Materiel[]>(`${this.materielsUrl}/maintenance`);
    }

    getMaterielEnPanne(): Observable<Materiel[]> {
        return this.http.get<Materiel[]>(`${this.materielsUrl}/panne`);
    }

    getMaterielNecessitantMaintenance(): Observable<Materiel[]> {
        return this.http.get<Materiel[]>(`${this.materielsUrl}/maintenance/requise`);
    }

    rechercherMateriel(terme: string): Observable<Materiel[]> {
        return this.http.get<Materiel[]>(`${this.materielsUrl}/recherche`, {
            params: new HttpParams().set('terme', terme)
        });
    }

    creerMateriel(request: CreateMaterielRequest): Observable<Materiel> {
        return this.http.post<Materiel>(this.materielsUrl, request);
    }

    updateMateriel(id: number, materiel: Materiel): Observable<Materiel> {
        return this.http.put<Materiel>(`${this.materielsUrl}/${id}`, materiel);
    }

    changerEtatMateriel(id: number, etat: string): Observable<Materiel> {
        return this.http.patch<Materiel>(`${this.materielsUrl}/${id}/etat`, null, {
            params: new HttpParams().set('etat', etat)
        });
    }

    programmerMaintenance(id: number, dateMaintenance: string): Observable<Materiel> {
        return this.http.patch<Materiel>(`${this.materielsUrl}/${id}/maintenance`, null, {
            params: new HttpParams().set('dateMaintenance', dateMaintenance)
        });
    }

    deplacerMateriel(materielId: number, nouvelleSalleId: number): Observable<Materiel> {
        return this.http.patch<Materiel>(`${this.materielsUrl}/${materielId}/deplacer/${nouvelleSalleId}`, null);
    }

    deleteMateriel(id: number): Observable<void> {
        return this.http.delete<void>(`${this.materielsUrl}/${id}`);
    }

    getStatistiquesMateriel(): Observable<any> {
        return this.http.get<any>(`${this.materielsUrl}/statistiques`);
    }

    // ─── RÉSERVATIONS ──────────────────────────────────────────

    getAllReservations(): Observable<Reservation[]> {
        return this.http.get<Reservation[]>(this.reservationsUrl);
    }

    getReservationById(id: number): Observable<Reservation> {
        return this.http.get<Reservation>(`${this.reservationsUrl}/${id}`);
    }

    getReservationsParSalle(salleId: number): Observable<Reservation[]> {
        return this.http.get<Reservation[]>(`${this.reservationsUrl}/salle/${salleId}`);
    }

    getReservationsParUtilisateur(utilisateurId: number): Observable<Reservation[]> {
        return this.http.get<Reservation[]>(`${this.reservationsUrl}/utilisateur/${utilisateurId}`);
    }

    getReservationsEnAttente(): Observable<Reservation[]> {
        return this.http.get<Reservation[]>(`${this.reservationsUrl}/en-attente`);
    }

    getReservationsActives(): Observable<Reservation[]> {
        return this.http.get<Reservation[]>(`${this.reservationsUrl}/actives`);
    }

    getReservationsPourPeriode(dateDebut: string, dateFin: string): Observable<Reservation[]> {
        const params = new HttpParams().set('dateDebut', dateDebut).set('dateFin', dateFin);
        return this.http.get<Reservation[]>(`${this.reservationsUrl}/periode`, { params });
    }

    creerReservation(reservation: Reservation): Observable<Reservation> {
        return this.http.post<Reservation>(this.reservationsUrl, reservation);
    }

    updateReservation(id: number, reservation: Reservation): Observable<Reservation> {
        return this.http.put<Reservation>(`${this.reservationsUrl}/${id}`, reservation);
    }

    validerReservation(id: number, validateurId: number): Observable<Reservation> {
        return this.http.patch<Reservation>(`${this.reservationsUrl}/${id}/valider`, null, {
            params: new HttpParams().set('validateurId', validateurId.toString())
        });
    }

    rejeterReservation(id: number, motifRejet: string): Observable<Reservation> {
        return this.http.patch<Reservation>(`${this.reservationsUrl}/${id}/rejeter`, null, {
            params: new HttpParams().set('motifRejet', motifRejet)
        });
    }

    annulerReservation(id: number): Observable<Reservation> {
        return this.http.patch<Reservation>(`${this.reservationsUrl}/${id}/annuler`, null);
    }

    deleteReservation(id: number): Observable<void> {
        return this.http.delete<void>(`${this.reservationsUrl}/${id}`);
    }

    getStatistiquesReservations(): Observable<any> {
        return this.http.get<any>(`${this.reservationsUrl}/statistiques`);
    }
}
