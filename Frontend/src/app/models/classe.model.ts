export interface Classe {
    id?: number;
    nom: string;
    code: string;
    specialite?: string;
    effectif: number;
    ecoleId: number;
    filiereId: number;
    salleId?: number;
    codeSalle?: string;
}
