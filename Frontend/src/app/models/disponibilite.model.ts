export interface Disponibilite {
    id?: number;
    enseignantId?: number;
    jour: string; // 'LUNDI', 'MARDI', etc.
    heureDebut: string; // Format 'HH:mm'
    heureFin: string;   // Format 'HH:mm'
    type: string;       // 'MATIN', 'APRES_MIDI', etc.
    commentaire?: string;
    estDisponible?: boolean;
}
