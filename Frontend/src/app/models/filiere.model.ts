import { Classe } from "./classe.model";

export interface Filiere {
    id: number;
    code: string;
    nom: string;
    ecoleId: number;
    classes?: Classe[];
}
