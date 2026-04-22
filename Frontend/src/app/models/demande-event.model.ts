export enum StatutDemande {
    VALIDE = 'VALIDE',
    REFUSE = 'REFUSE',
    EN_ATTENTE = 'EN_ATTENTE'
}

export interface DemandeEvent {
    id?: number;
    titre: string;
    objectif: string;
    debutEvent: string | Date;
    finEvent: string | Date;
    nbreLimitParticipant: number;
    enseignantId: number;
    status?: StatutDemande;
}
