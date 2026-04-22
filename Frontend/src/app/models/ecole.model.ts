import { Classe } from "./classe.model";
import { Filiere } from "./filiere.model";

export interface Ecole {
    id?: number;
    code: string;
    nom: string;
    description?: string;
    classes?: Classe[];
    filieres?: Filiere[];
}
