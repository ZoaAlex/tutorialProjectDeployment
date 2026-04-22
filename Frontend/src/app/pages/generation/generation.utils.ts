import { ResultatGeneration, PlacementEffectue } from '../../services/planning.service';

export interface EmploiSauvegarde {
  semaine: string;
  date: string;
  resultat: ResultatGeneration;
}

/**
 * Normalise une heure au format HH:mm:ss ou HH:mm en HH:mm (5 premiers caractères).
 * Property 2 — Validates: Requirements 3.3
 */
export function normaliserHeure(heure: string): string {
  return (heure ?? '').substring(0, 5);
}

/**
 * Filtre les placements par classeId.
 * Si classeId est null, retourne tous les placements sans modification.
 * Property 1 — Validates: Requirements 2.2, 2.3
 */
export function filtrerParClasse(
  placements: PlacementEffectue[],
  classeId: number | null
): PlacementEffectue[] {
  if (classeId === null) return placements;
  return placements.filter(p => p.classeId === classeId);
}

const STORAGE_KEY = 'emplois_du_temps';

export function sauvegarderResultat(
  semaine: string,
  res: ResultatGeneration,
  storage: Storage
): EmploiSauvegarde[] {
  const existing = chargerHistorique(storage).filter(h => h.semaine !== semaine);
  const entry: EmploiSauvegarde = {
    semaine,
    date: new Date().toLocaleDateString('fr-FR'),
    resultat: res
  };
  const updated = [entry, ...existing];
  storage.setItem(STORAGE_KEY, JSON.stringify(updated));
  return updated;
}

export function supprimerSemaine(
  semaine: string,
  storage: Storage
): EmploiSauvegarde[] {
  const updated = chargerHistorique(storage).filter(h => h.semaine !== semaine);
  storage.setItem(STORAGE_KEY, JSON.stringify(updated));
  return updated;
}

export function chargerHistorique(storage: Storage): EmploiSauvegarde[] {
  try {
    const data = storage.getItem(STORAGE_KEY);
    return data ? JSON.parse(data) : [];
  } catch {
    return [];
  }
}
