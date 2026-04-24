export enum TypeCours {
  COURS_MAGISTRAL = 'COURS_MAGISTRAL',
  TRAVAUX_DIRIGES = 'TRAVAUX_DIRIGES',
  TRAVAUX_PRATIQUES = 'TRAVAUX_PRATIQUES',
  EXAMEN = 'EXAMEN',
  SOUTENANCE = 'SOUTENANCE'
}

export enum StatutCours {
  PLACE = 'place',
  EN_ATTENTE = 'en_attente'
}

export interface Cours {
  id: number;
  nom: string;
  statutCours: StatutCours;
  type?: StatutCours;
  classeId?: number;
  codeClasse?: string;
  ueId?: number;
  volumeHoraire: number;
  nbreheurefait: number;
  enseignantEmail?: string;
  effectifClasse?: number;
}
