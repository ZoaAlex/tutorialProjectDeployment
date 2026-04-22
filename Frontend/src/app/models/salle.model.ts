import { Materiel } from './materiel.model';

// Valeurs exactes du backend StatutSalle.java
export enum StatutSalle {
    LIBRE = 'LIBRE',
    OCCUPEE = 'OCCUPEE',
    MAINTENANCE = 'MAINTENANCE',
    HORS_SERVICE = 'HORS_SERVICE'
}

// Valeurs exactes du backend TypeSalle.java
export enum TypeSalle {
    AMPHITHEATRE      = 'AMPHITHEATRE',
    SALLE_COURS       = 'SALLE_COURS',
    LABORATOIRE       = 'LABORATOIRE',
    SALLE_INFORMATIQUE= 'SALLE_INFORMATIQUE',
    SALLE_CONFERENCE  = 'SALLE_CONFERENCE',
    BIBLIOTHEQUE      = 'BIBLIOTHEQUE',
    BUREAU            = 'BUREAU',
    AUTRE             = 'AUTRE'
}

// Correspond exactement au SalleDTO retourné par le backend
export interface Salle {
    id?: number;
    codeSalle: string;
    nom: string;
    capacite: number;
    typeSalle: TypeSalle;
    statut: StatutSalle;
    description?: string;
    emplacement?: string;
    ecoleId: number;
    etage?: number;
    batiment?: string;
    surface?: number;
    accessibleHandicap?: boolean;
    climatisee?: boolean;
    wifiDisponible?: boolean;
    dateCreation?: string;
    dateModification?: string;
    creePar?: string;
    modifiePar?: string;
    // Champs enrichis retournés par SalleDTO (via Feign vers coursclasse-service)
    nomEcole?: string;
    codeEcole?: string;
    nomUniversite?: string;
    nomCompletEcole?: string;
    // Champs calculés
    materiels?: Materiel[];
    nombreReservationsActives?: number;
    disponibleMaintenant?: boolean;
}

// Utilisé pour POST /api/salles — correspond à CreateSalleRequest.java
export interface CreateSalleRequest {
    codeSalle: string;
    nom: string;
    capacite: number;
    typeSalle: TypeSalle;
    description?: string;
    emplacement?: string;
    etage?: number;
    batiment?: string;
    surface?: number;
    accessibleHandicap?: boolean;
    climatisee?: boolean;
    wifiDisponible?: boolean;
}

// Critères de recherche — correspond à SalleSearchCriteria.java (POST /api/salles/recherche)
export interface SalleSearchCriteria {
    ecoleId?: number;
    typeSalle?: TypeSalle;
    capaciteMin?: number;
    capaciteMax?: number;
    statut?: StatutSalle;
    accessibleHandicap?: boolean;
    climatisee?: boolean;
    wifiDisponible?: boolean;
    batiment?: string;
    etage?: number;
    searchTerm?: string;
    // Critères de disponibilité
    dateDebut?: string;
    dateFin?: string;
    disponiblePourPeriode?: boolean;
    // Critères matériel
    typeMaterielRequis?: string;
    avecMaterielFonctionnel?: boolean;
}

// Correspond à la réponse de GET /api/salles/statistiques
export interface StatistiquesSalles {
    totalSalles: number;
    sallesDisponibles: number;
    sallesOccupees: number;
    sallesEnMaintenance: number;
    parEcole: Array<[number, number]>;
    parStatut: Array<[string, number]>;
}
