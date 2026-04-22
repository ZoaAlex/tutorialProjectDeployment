// Valeurs exactes du backend TypeMateriel.java
export enum TypeMateriel {
    PROJECTEUR = 'PROJECTEUR',
    ORDINATEUR = 'ORDINATEUR',
    TABLEAU_INTERACTIF = 'TABLEAU_INTERACTIF',
    MICRO = 'MICRO',
    HAUT_PARLEUR = 'HAUT_PARLEUR',
    CAMERA = 'CAMERA',
    ECRAN = 'ECRAN',
    CLIMATISATION = 'CLIMATISATION',
    WIFI = 'WIFI',
    AUTRE = 'AUTRE'
}

// Valeurs exactes du backend (champ String dans Materiel.java)
export enum EtatMateriel {
    FONCTIONNEL = 'FONCTIONNEL',
    EN_PANNE = 'EN_PANNE',
    EN_MAINTENANCE = 'EN_MAINTENANCE'
}

export interface Materiel {
    id: number;
    nom: string;
    type: TypeMateriel;
    description?: string;
    quantite: number;
    quantiteFonctionnelle?: number;
    marque?: string;
    modele?: string;
    numeroSerie?: string;
    dateAcquisition?: string;
    dateDerniereMaintenance?: string;
    dateProchaineMaintenance?: string;
    etat: EtatMateriel;
    observations?: string;
    salleId: number;
    nomSalle?: string;
    dateCreation?: string;
    dateModification?: string;
    creePar?: string;
    modifiePar?: string;
    // Champs calculés retournés par le backend
    maintenanceRequise?: boolean;
    joursDepuisDerniereMaintenance?: number;
    joursAvantProchaineMaintenance?: number;
}

export interface CreateMaterielRequest {
    nom: string;
    type: TypeMateriel;
    description?: string;
    quantite: number;
    quantiteFonctionnelle?: number;
    marque?: string;
    modele?: string;
    numeroSerie?: string;
    dateAcquisition?: string;
    dateProchaineMaintenance?: string;
    observations?: string;
    salleId: number;
}
