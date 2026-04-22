export enum Sexe {
    MASCULINE = 'MASCULINE',
    FEMININE = 'FEMININE'
}

export interface Etudiant {
    id?: number;
    matricule: string;
    nom: string;
    prenom: string;
    sex: Sexe;
    classeId: number;
    ecoleId: number;
    groupeIds?: number[];
}
