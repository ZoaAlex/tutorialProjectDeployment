import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import {
  PlanningService,
  PlacementEffectue,
  UpdateEmploiDuTempsRequest
} from '../../services/planning.service';
import { CoursClasseService } from '../../services/cours-classe.service';
import { SalleService } from '../../services/salle.service';
import { UserService } from '../../services/user.service';
import { Classe } from '../../models/classe.model';
import { Cours } from '../../models/cours.model';
import { Salle } from '../../models/salle.model';
import { User } from '../../models/user.model';
import { normaliserHeure } from '../generation/generation.utils';

const JOURS = ['LUNDI', 'MARDI', 'MERCREDI', 'JEUDI', 'VENDREDI', 'SAMEDI'];
const CRENEAUX = [
  { debut: '08:00', fin: '10:00' },
  { debut: '10:00', fin: '12:00' },
  { debut: '13:00', fin: '15:00' },
  { debut: '15:00', fin: '17:00' },
];

export interface PlacementAvecId extends PlacementEffectue {
  id?: number;
  modifie?: boolean; // marqué dirty localement
}

@Component({
  selector: 'app-emploi-du-temps-editor',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, NavbarComponent],
  styles: [`
    .editor-layout { display: flex; gap: 16px; }
    .sidebar-cours { width: 220px; flex-shrink: 0; }
    .calendrier-wrap { flex: 1; overflow-x: auto; }
    .table-cal { border-collapse: collapse; width: 100%; min-width: 700px; }
    .table-cal th { background: #2d3748; color: #fff; text-align: center; padding: 8px; font-size: 0.82rem; }
    .table-cal td { border: 1px solid #e2e8f0; vertical-align: top; padding: 4px; min-height: 72px; width: 14%; }
    .td-heure { background: #f7fafc; font-weight: 600; font-size: 0.78rem; color: #4a5568; text-align: center; width: 72px; }
    .drop-zone { min-height: 68px; border-radius: 4px; transition: background 0.15s; }
    .drop-zone.drag-over { background: #ebf4ff; border: 2px dashed #667eea; }
    .cours-card {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #fff; border-radius: 6px; padding: 5px 7px; font-size: 0.75rem;
      margin: 2px 0; cursor: grab; user-select: none; position: relative;
    }
    .cours-card.dirty { outline: 2px solid #ffc107; }
    .cours-card:active { cursor: grabbing; }
    .cours-card .cours-nom { font-weight: 700; font-size: 0.78rem; }
    .cours-card .cours-meta { opacity: 0.88; font-size: 0.7rem; margin-top: 1px; }
    .cours-card .btn-edit {
      position: absolute; top: 3px; right: 3px;
      background: rgba(255,255,255,0.25); border: none; border-radius: 3px;
      color: #fff; font-size: 0.65rem; padding: 1px 4px; cursor: pointer;
    }
    .cours-card .btn-remove {
      position: absolute; bottom: 3px; right: 3px;
      background: rgba(220,53,69,0.7); border: none; border-radius: 3px;
      color: #fff; font-size: 0.65rem; padding: 1px 4px; cursor: pointer;
    }
    .non-place-card {
      background: #fff3cd; border: 1px solid #ffc107; border-radius: 6px;
      padding: 6px 8px; font-size: 0.75rem; margin-bottom: 6px;
      cursor: grab; user-select: none;
    }
    .non-place-card:active { cursor: grabbing; }
    .non-place-card .np-nom { font-weight: 600; color: #856404; }
    .non-place-card .np-meta { color: #6c757d; font-size: 0.7rem; }
    .saving-overlay { opacity: 0.6; pointer-events: none; }
    .dirty-badge { font-size: 0.7rem; }
  `],
  templateUrl: './emploi-du-temps-editor.component.html'
})
export class EmploiDuTempsEditorComponent implements OnInit {
  private planningService = inject(PlanningService);
  private coursClasseService = inject(CoursClasseService);
  private salleService = inject(SalleService);
  private userService = inject(UserService);

  readonly jours = JOURS;
  readonly creneaux = CRENEAUX;

  classes = signal<Classe[]>([]);
  classeSelectionnee = signal<Classe | null>(null);
  placements = signal<PlacementAvecId[]>([]);
  coursNonPlaces = signal<Cours[]>([]);
  salles = signal<Salle[]>([]);
  enseignants = signal<User[]>([]);

  loading = signal(false);
  saving = signal(false);
  erreur = signal<string | null>(null);
  succes = signal<string | null>(null);

  // Drag state
  draggedNonPlace = signal<Cours | null>(null);
  draggedPlacement = signal<PlacementAvecId | null>(null);
  dragOverCell = signal<string | null>(null);

  // Modal édition
  showModal = signal(false);
  modalPlacement = signal<PlacementAvecId | null>(null);
  modalJour = signal('');
  modalHeure = signal('');
  modalSalleId = signal<number | null>(null);
  modalEnseignantId = signal<number | null>(null);

  placementsFiltres = computed<PlacementAvecId[]>(() => {
    const classe = this.classeSelectionnee();
    if (!classe) return [];
    return this.placements().filter(p => p.classeId === classe.id);
  });

  modifiesCount = computed(() => this.placementsFiltres().filter(p => p.modifie).length);

  ngOnInit(): void {
    this.coursClasseService.getAllClasses().subscribe(c => this.classes.set(c));
    this.salleService.getAllSalles().subscribe(s => this.salles.set(s));
    this.userService.getEnseignants().subscribe(e => this.enseignants.set(e));
    this.chargerPlacements();
  }

  chargerPlacements(): void {
    this.loading.set(true);
    this.planningService.getEmploiDuTemps().subscribe({
      next: (data) => { this.placements.set(data as PlacementAvecId[]); this.loading.set(false); },
      error: () => { this.erreur.set('Impossible de charger l\'emploi du temps.'); this.loading.set(false); }
    });
  }

  onClasseChange(event: Event): void {
    const id = Number((event.target as HTMLSelectElement).value);
    const classe = this.classes().find(c => c.id === id) ?? null;
    this.classeSelectionnee.set(classe);
    if (classe) this.chargerCoursNonPlaces(classe.id!);
  }

  chargerCoursNonPlaces(classeId: number): void {
    this.coursClasseService.getAllCours().subscribe(tous => {
      const placesIds = new Set(this.placementsFiltres().map(p => p.coursId));
      this.coursNonPlaces.set(tous.filter(c => c.classeId === classeId && !placesIds.has(c.id)));
    });
  }

  getPlacementsCase(jour: string, heureDebut: string): PlacementAvecId[] {
    return this.placementsFiltres().filter(p =>
      p.jour?.toUpperCase() === jour &&
      normaliserHeure(p.heureDebut ?? '') === heureDebut
    );
  }

  getNomEnseignant(id: number | null): string {
    if (!id) return 'N/A';
    const e = this.enseignants().find(u => u.id === id);
    return e ? `${e.prenom ?? ''} ${e.nom ?? ''}`.trim() : `Ens. #${id}`;
  }

  // ─── DRAG & DROP ────────────────────────────────────────────

  onDragStartNonPlace(cours: Cours): void {
    this.draggedNonPlace.set(cours);
    this.draggedPlacement.set(null);
  }

  onDragStartPlacement(p: PlacementAvecId): void {
    this.draggedPlacement.set(p);
    this.draggedNonPlace.set(null);
  }

  onDragOver(event: DragEvent, jour: string, heure: string): void {
    event.preventDefault();
    this.dragOverCell.set(`${jour}-${heure}`);
  }

  onDragLeave(): void { this.dragOverCell.set(null); }

  onDrop(event: DragEvent, jour: string, heureDebut: string): void {
    event.preventDefault();
    this.dragOverCell.set(null);
    const creneau = CRENEAUX.find(c => c.debut === heureDebut)!;

    const nonPlace = this.draggedNonPlace();
    const placement = this.draggedPlacement();

    if (nonPlace) this.placerCoursNonPlace(nonPlace, jour, creneau);
    else if (placement) this.deplacerPlacement(placement, jour, creneau);

    this.draggedNonPlace.set(null);
    this.draggedPlacement.set(null);
  }

  private placerCoursNonPlace(cours: Cours, jour: string, creneau: { debut: string; fin: string }): void {
    const salle = this.salles()[0];
    if (!salle) { this.erreur.set('Aucune salle disponible.'); return; }

    const nouveau: PlacementAvecId = {
      coursId: cours.id,
      nomCours: cours.nom,
      enseignantId: 0,
      classeId: cours.classeId ?? this.classeSelectionnee()!.id!,
      salleId: salle.id!,
      nomSalle: salle.nom,
      jour,
      heureDebut: creneau.debut,
      heureFin: creneau.fin,
      score: 0,
      modifie: true
    };

    this.placements.update(list => [...list, nouveau]);
    this.coursNonPlaces.update(list => list.filter(c => c.id !== cours.id));
  }

  private deplacerPlacement(p: PlacementAvecId, jour: string, creneau: { debut: string; fin: string }): void {
    this.placements.update(list =>
      list.map(x => x === p
        ? { ...x, jour, heureDebut: creneau.debut, heureFin: creneau.fin, modifie: true }
        : x)
    );
  }

  // ─── MODAL ÉDITION ──────────────────────────────────────────

  ouvrirModal(p: PlacementAvecId): void {
    this.modalPlacement.set(p);
    this.modalJour.set(p.jour);
    this.modalHeure.set(normaliserHeure(p.heureDebut));
    this.modalSalleId.set(p.salleId ?? null);
    this.modalEnseignantId.set(p.enseignantId ?? null);
    this.showModal.set(true);
  }

  fermerModal(): void { this.showModal.set(false); this.modalPlacement.set(null); }

  sauvegarderModal(): void {
    const p = this.modalPlacement();
    if (!p) return;
    const creneau = CRENEAUX.find(c => c.debut === this.modalHeure());
    const salle = this.salles().find(s => s.id === this.modalSalleId());

    this.placements.update(list =>
      list.map(x => x === p ? {
        ...x,
        jour: this.modalJour(),
        heureDebut: this.modalHeure(),
        heureFin: creneau?.fin ?? x.heureFin,
        salleId: this.modalSalleId() ?? x.salleId,
        nomSalle: salle?.nom ?? x.nomSalle,
        enseignantId: this.modalEnseignantId() ?? x.enseignantId,
        modifie: true
      } : x)
    );
    this.fermerModal();
  }

  supprimerPlacement(p: PlacementAvecId): void {
    if (!confirm(`Supprimer le cours "${p.nomCours}" de ce créneau ?`)) return;

    if (!p.id) {
      // Pas encore persisté — suppression locale uniquement
      this.placements.update(list => list.filter(x => x !== p));
      this.chargerCoursNonPlaces(this.classeSelectionnee()!.id!);
      return;
    }

    this.saving.set(true);
    this.planningService.deleteEmploiDuTemps(p.id).subscribe({
      next: () => {
        this.placements.update(list => list.filter(x => (x as any).id !== p.id));
        this.chargerCoursNonPlaces(this.classeSelectionnee()!.id!);
        this.saving.set(false);
        this.afficherSucces('Créneau supprimé.');
      },
      error: () => { this.erreur.set('Erreur lors de la suppression.'); this.saving.set(false); }
    });
  }

  // ─── ENREGISTRER TOUT ───────────────────────────────────────

  enregistrerModifications(): void {
    const modifies = this.placementsFiltres().filter(p => p.modifie && p.id);
    if (modifies.length === 0) {
      this.afficherSucces('Aucune modification à enregistrer.');
      return;
    }

    this.saving.set(true);
    const appels = modifies.map(p => {
      const request: UpdateEmploiDuTempsRequest = {
        jour: p.jour,
        heureDebut: p.heureDebut,
        heureFin: p.heureFin,
        salleId: p.salleId,
        nomSalle: p.nomSalle,
        enseignantId: p.enseignantId
      };
      return this.planningService.updateEmploiDuTemps(p.id!, request);
    });

    forkJoin(appels).subscribe({
      next: () => {
        // Marquer tous comme non-modifiés
        this.placements.update(list => list.map(x => ({ ...x, modifie: false })));
        this.saving.set(false);
        this.afficherSucces(`${modifies.length} créneau(x) enregistré(s).`);
      },
      error: () => { this.erreur.set('Erreur lors de l\'enregistrement.'); this.saving.set(false); }
    });
  }

  private afficherSucces(msg: string): void {
    this.succes.set(msg);
    this.erreur.set(null);
    setTimeout(() => this.succes.set(null), 3000);
  }

  cellKey(jour: string, heure: string): string { return `${jour}-${heure}`; }
}
