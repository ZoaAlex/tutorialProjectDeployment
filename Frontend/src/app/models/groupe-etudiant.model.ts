export interface GroupeEtudiant {
    id?: number;
    nom: string;
    description?: string;
    effectif?: number;
    classeId?: number;
    classeIds?: number[];
    etudiantIds?: number[];
}
