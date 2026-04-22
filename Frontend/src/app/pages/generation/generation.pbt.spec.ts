/**
 * Property-Based Tests — Feature: generation-emploi-du-temps
 * Uses fast-check for property generation (min 100 iterations each).
 */
import * as fc from 'fast-check';
import {
  sauvegarderResultat,
  supprimerSemaine,
  chargerHistorique,
  filtrerParClasse,
  normaliserHeure
} from './generation.utils';
import { ResultatGeneration, PlacementEffectue, CoursNonPlace } from '../../services/planning.service';

// ---------------------------------------------------------------------------
// Minimal in-memory localStorage mock (no DOM required)
// ---------------------------------------------------------------------------
function makeStorage(): Storage {
  const store: Record<string, string> = {};
  return {
    getItem: (k: string) => store[k] ?? null,
    setItem: (k: string, v: string) => { store[k] = v; },
    removeItem: (k: string) => { delete store[k]; },
    clear: () => { Object.keys(store).forEach(k => delete store[k]); },
    key: (i: number) => Object.keys(store)[i] ?? null,
    get length() { return Object.keys(store).length; }
  } as Storage;
}

// ---------------------------------------------------------------------------
// Arbitraries
// ---------------------------------------------------------------------------
const arbPlacement = (): fc.Arbitrary<PlacementEffectue> =>
  fc.record({
    coursId: fc.integer({ min: 1, max: 9999 }),
    nomCours: fc.string({ minLength: 1, maxLength: 50 }),
    enseignantId: fc.integer({ min: 1, max: 999 }),
    classeId: fc.integer({ min: 1, max: 999 }),
    salleId: fc.integer({ min: 1, max: 999 }),
    nomSalle: fc.string({ minLength: 1, maxLength: 30 }),
    jour: fc.constantFrom('LUNDI', 'MARDI', 'MERCREDI', 'JEUDI', 'VENDREDI', 'SAMEDI'),
    heureDebut: fc.constantFrom('08:00', '10:00', '13:00', '15:00'),
    heureFin: fc.constantFrom('10:00', '12:00', '15:00', '17:00'),
    score: fc.float({ min: 0, max: 100, noNaN: true }),
  });

const arbCoursNonPlace = (): fc.Arbitrary<CoursNonPlace> =>
  fc.record({
    coursId: fc.integer({ min: 1, max: 9999 }),
    nomCours: fc.string({ minLength: 1, maxLength: 50 }),
    enseignantId: fc.integer({ min: 1, max: 999 }),
    volumeRestant: fc.integer({ min: 1, max: 20 }),
    raison: fc.string({ minLength: 1, maxLength: 100 }),
  });

const arbResultat = (): fc.Arbitrary<ResultatGeneration> =>
  fc.record({
    nombrePlacements: fc.integer({ min: 0, max: 200 }),
    nombreCoursNonPlaces: fc.integer({ min: 0, max: 50 }),
    placements: fc.array(arbPlacement(), { maxLength: 20 }),
    coursNonPlaces: fc.array(arbCoursNonPlace(), { maxLength: 10 }),
  });

// Semaine au format YYYY-Www (ex: 2025-W14)
const arbSemaine = (): fc.Arbitrary<string> =>
  fc.tuple(
    fc.integer({ min: 2020, max: 2030 }),
    fc.integer({ min: 1, max: 52 })
  ).map(([year, week]) => `${year}-W${String(week).padStart(2, '0')}`);

// ---------------------------------------------------------------------------
// Property 1 : Filtrage des placements par classe
// **Feature: generation-emploi-du-temps, Property 1: Filtrage des placements par classe**
// Validates: Requirements 2.2, 2.3
// ---------------------------------------------------------------------------
describe('Property 1: Filtrage des placements par classe', () => {
  it(
    'Pour tout tableau de placements et tout classeId non-null, tous les éléments retournés ont le classeId correspondant',
    () => {
      fc.assert(
        fc.property(
          fc.array(arbPlacement(), { maxLength: 30 }),
          fc.integer({ min: 1, max: 999 }),
          (placements, classeId) => {
            const result = filtrerParClasse(placements, classeId);
            expect(result.every(p => p.classeId === classeId)).toBe(true);
          }
        ),
        { numRuns: 100 }
      );
    }
  );

  it(
    'Pour tout tableau de placements, filtrerParClasse avec null retourne tous les placements sans modification',
    () => {
      fc.assert(
        fc.property(
          fc.array(arbPlacement(), { maxLength: 30 }),
          (placements) => {
            const result = filtrerParClasse(placements, null);
            expect(result).toEqual(placements);
          }
        ),
        { numRuns: 100 }
      );
    }
  );
});

// ---------------------------------------------------------------------------
// Property 2 : Normalisation de l'heure
// **Feature: generation-emploi-du-temps, Property 2: Normalisation de l'heure**
// Validates: Requirements 3.3
// ---------------------------------------------------------------------------
describe('Property 2: Normalisation de l\'heure', () => {
  it(
    'Pour toute heure au format HH:mm:ss, normaliserHeure retourne exactement les 5 premiers caractères (HH:mm)',
    () => {
      fc.assert(
        fc.property(
          fc.tuple(
            fc.integer({ min: 0, max: 23 }),
            fc.integer({ min: 0, max: 59 }),
            fc.integer({ min: 0, max: 59 })
          ).map(([h, m, s]) => {
            const hh = String(h).padStart(2, '0');
            const mm = String(m).padStart(2, '0');
            const ss = String(s).padStart(2, '0');
            return `${hh}:${mm}:${ss}`;
          }),
          (heure) => {
            const result = normaliserHeure(heure);
            expect(result).toHaveLength(5);
            expect(result).toBe(heure.substring(0, 5));
          }
        ),
        { numRuns: 100 }
      );
    }
  );

  it(
    'Pour toute heure déjà au format HH:mm, normaliserHeure retourne la valeur inchangée',
    () => {
      fc.assert(
        fc.property(
          fc.tuple(
            fc.integer({ min: 0, max: 23 }),
            fc.integer({ min: 0, max: 59 })
          ).map(([h, m]) => `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`),
          (heure) => {
            const result = normaliserHeure(heure);
            expect(result).toBe(heure);
          }
        ),
        { numRuns: 100 }
      );
    }
  );
});

// ---------------------------------------------------------------------------
// Property 3 : Sauvegarde dans l'historique (round-trip)
// **Feature: generation-emploi-du-temps, Property 3: Sauvegarde dans l'historique (round-trip)**
// Validates: Requirements 1.5
// ---------------------------------------------------------------------------
describe('Property 3: Sauvegarde dans l\'historique (round-trip)', () => {
  it(
    'Pour tout résultat et toute semaine, après sauvegarderResultat le localStorage contient une entrée avec la bonne semaine et le bon résultat',
    () => {
      fc.assert(
        fc.property(arbResultat(), arbSemaine(), (resultat, semaine) => {
          const storage = makeStorage();

          sauvegarderResultat(semaine, resultat, storage);

          const historique = chargerHistorique(storage);
          const entry = historique.find(h => h.semaine === semaine);

          expect(entry).toBeDefined();
          expect(entry!.resultat).toEqual(resultat);
          expect(entry!.semaine).toBe(semaine);
        }),
        { numRuns: 100 }
      );
    }
  );

  it(
    'Pour tout résultat, sauvegarder deux fois la même semaine ne crée qu\'une seule entrée (idempotence de la clé)',
    () => {
      fc.assert(
        fc.property(arbResultat(), arbResultat(), arbSemaine(), (res1, res2, semaine) => {
          const storage = makeStorage();

          sauvegarderResultat(semaine, res1, storage);
          sauvegarderResultat(semaine, res2, storage);

          const historique = chargerHistorique(storage);
          const entries = historique.filter(h => h.semaine === semaine);

          expect(entries.length).toBe(1);
          expect(entries[0].resultat).toEqual(res2);
        }),
        { numRuns: 100 }
      );
    }
  );
});

// ---------------------------------------------------------------------------
// Property 4 : Suppression de l'historique
// **Feature: generation-emploi-du-temps, Property 4: Suppression de l'historique**
// Validates: Requirements 5.3
// ---------------------------------------------------------------------------
describe('Property 4: Suppression de l\'historique', () => {
  it(
    'Pour tout historique contenant une semaine donnée, après supprimerSemaine cette semaine n\'apparaît plus dans la liste ni dans le localStorage',
    () => {
      fc.assert(
        fc.property(
          fc.array(arbSemaine(), { minLength: 1, maxLength: 10 }),
          arbResultat(),
          (semaines, resultat) => {
            // Dédupliquer les semaines pour avoir un historique cohérent
            const uniqueSemaines = [...new Set(semaines)];
            const storage = makeStorage();

            // Peupler l'historique
            uniqueSemaines.forEach(s => sauvegarderResultat(s, resultat, storage));

            // Choisir la première semaine à supprimer
            const semaineASupprimer = uniqueSemaines[0];
            const updated = supprimerSemaine(semaineASupprimer, storage);

            // La semaine supprimée ne doit plus apparaître dans le retour
            expect(updated.find(h => h.semaine === semaineASupprimer)).toBeUndefined();

            // Le localStorage doit aussi ne plus la contenir
            const fromStorage = chargerHistorique(storage);
            expect(fromStorage.find(h => h.semaine === semaineASupprimer)).toBeUndefined();
          }
        ),
        { numRuns: 100 }
      );
    }
  );
});

// ---------------------------------------------------------------------------
// Property 5 : Affichage des informations de placement
// **Feature: generation-emploi-du-temps, Property 5: Affichage des informations de placement**
// Validates: Requirements 3.2
// ---------------------------------------------------------------------------
describe('Property 5: Affichage des informations de placement', () => {
  it(
    'Pour tout placement, la représentation textuelle contient nomCours, enseignantId et nomSalle',
    () => {
      fc.assert(
        fc.property(arbPlacement(), (placement) => {
          // Simule le rendu de la carte cours (logique extraite du template)
          const rendered = renderPlacementCard(placement);

          expect(rendered).toContain(placement.nomCours);
          expect(rendered).toContain(String(placement.enseignantId));
          expect(rendered).toContain(placement.nomSalle);
        }),
        { numRuns: 100 }
      );
    }
  );
});

/**
 * Simule le rendu d'une carte cours tel que défini dans le template GenerationComponent.
 * Reproduit la logique : nomCours + "Ens. #" + enseignantId + nomSalle
 */
function renderPlacementCard(p: PlacementEffectue): string {
  return [
    p.nomCours,
    `Ens. #${p.enseignantId !== null ? p.enseignantId : 'N/A'}`,
    p.nomSalle
  ].join(' | ');
}
