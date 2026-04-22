export enum StatutReservation {
    EN_ATTENTE = 'EN_ATTENTE',
    VALIDEE = 'VALIDEE',
    REJETEE = 'REJETEE',
    ANNULEE = 'ANNULEE',
    TERMINEE = 'TERMINEE'
}

export interface Reservation {
    id?: number;
    dateDebut: Date | string;
    dateFin: Date | string;
    motif: string;
    description?: string;
    statut: StatutReservation;
    utilisateurId: number;
    nombreParticipants?: number;
    materielRequis?: string;
    commentaires?: string;
    valideePar?: number;
    dateValidation?: Date;
    motifRejet?: string;
    priorite?: number;
    recurrente?: boolean;
    frequenceRecurrence?: string;
    dateFinRecurrence?: Date;
    salleId: number;
    dateCreation?: Date;
    dateModification?: Date;
    creePar?: string;
    modifiePar?: string;
}
