export enum TypeCours {
  COURS_MAGISTRAL = 'COURS_MAGISTRAL',
  TRAVAUX_DIRIGES = 'TRAVAUX_DIRIGES',
  TRAVAUX_PRATIQUES = 'TRAVAUX_PRATIQUES',
  EXAMEN = 'EXAMEN',
  SOUTENANCE = 'SOUTENANCE'
}

export enum StatutCours {
  ACTIF = 'ACTIF',
  SUSPENDU = 'SUSPENDU',
  ANNULE = 'ANNULE',
  REPORTE = 'REPORTE',
  TERMINE = 'TERMINE'
}

export interface Cours {
  id: number;
  nom: string;
  /** Aligné sur le champ "statutCours" du backend */
  statutCours: StatutCours;
  /** Alias conservé pour compatibilité avec les templates existants */
  type?: StatutCours;
  classeId?: number;
  ueId?: number;
  volumeHoraire: number;
  nbreheurefait: number;
  enseignantId?: number;
  codeClasse?: string;
  effectifClasse?: number;
}
