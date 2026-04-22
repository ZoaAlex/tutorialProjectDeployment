import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { PlanningService, ResultatGeneration, PlacementEffectue } from '../../services/planning.service';
import { CoursClasseService } from '../../services/cours-classe.service';
import { UserService } from '../../services/user.service';
import { Classe } from '../../models/classe.model';
import {
  EmploiSauvegarde,
  sauvegarderResultat,
  supprimerSemaine,
  chargerHistorique,
  filtrerParClasse,
  normaliserHeure
} from './generation.utils';

const JOURS = ['LUNDI', 'MARDI', 'MERCREDI', 'JEUDI', 'VENDREDI', 'SAMEDI'];
const CRENEAUX = ['08:00', '10:00', '13:00', '15:00'];

@Component({
  selector: 'app-generation',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, NavbarComponent],
  styles: [`
    .calendrier { overflow-x: auto; }
    .table-calendrier { min-width: 900px; border-collapse: collapse; width: 100%; }
    .table-calendrier th { background: #2d3748; color: #fff; text-align: center; padding: 10px; font-size: 0.85rem; }
    .table-calendrier td { border: 1px solid #e2e8f0; vertical-align: top; padding: 4px; min-height: 70px; width: 14%; }
    .td-heure { background: #f7fafc; font-weight: 600; font-size: 0.8rem; color: #4a5568; text-align: center; width: 80px; }
    .cours-card { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #fff;
      border-radius: 6px; padding: 6px 8px; font-size: 0.78rem; margin: 2px 0; }
    .cours-card .cours-nom { font-weight: 700; font-size: 0.82rem; }
    .cours-card .cours-info { opacity: 0.9; font-size: 0.72rem; margin-top: 2px; }
    .cours-card .cours-salle { opacity: 0.85; font-size: 0.7rem; }
    .historique-item { cursor: pointer; transition: background 0.15s; }
    .historique-item:hover { background: #f0f4ff; }
    .historique-item.active { background: #e8f0fe; border-left: 3px solid #667eea; }
    .badge-semaine { background: #667eea; color: #fff; border-radius: 12px; padding: 2px 10px; font-size: 0.78rem; }
  `],
  template: `
    <app-sidebar></app-sidebar>
    <div class="main-content">
      <app-navbar pageTitle="Génération Emploi du Temps"></app-navbar>
      <div class="content-wrapper">

        <!-- Tabs -->
        <ul class="nav nav-tabs mb-4">
          <li class="nav-item">
            <button class="nav-link" [class.active]="onglet() === 'generer'" (click)="onglet.set('generer')">
              <i class="fas fa-magic me-2"></i>Générer
            </button>
          </li>
          <li class="nav-item">
            <button class="nav-link" [class.active]="onglet() === 'historique'" (click)="onglet.set('historique')">
              <i class="fas fa-history me-2"></i>Historique
              @if (historique().length > 0) {
                <span class="badge bg-primary ms-1">{{ historique().length }}</span>
              }
            </button>
          </li>
        </ul>

        <!-- === ONGLET GENERER === -->
        @if (onglet() === 'generer') {
          <div class="card mb-4">
            <div class="card-body">
              <div class="row g-3 align-items-end">
                <div class="col-md-5">
                  <button class="btn btn-primary w-100" (click)="lancerGeneration()"
                          [disabled]="loading()">
                    @if (loading()) {
                      <span class="spinner-border spinner-border-sm me-2"></span>En cours...
                    } @else {
                      <i class="fas fa-magic me-2"></i>Générer l'emploi du temps
                    }
                  </button>
                </div>
              </div>
              @if (erreur()) {
                <div class="alert alert-danger mt-3 mb-0">
                  <i class="fas fa-exclamation-triangle me-2"></i>{{ erreur() }}
                </div>
              }
            </div>
          </div>

          @if (resultat()) {
            <!-- Stats -->
            <div class="row g-3 mb-4">
              <div class="col-md-4">
                <div class="card text-center">
                  <div class="card-body">
                    <div class="display-6 text-success fw-bold">{{ resultat()!.nombrePlacements }}</div>
                    <div class="text-muted">Cours placés</div>
                  </div>
                </div>
              </div>
              <div class="col-md-4">
                <div class="card text-center">
                  <div class="card-body">
                    <div class="display-6 text-danger fw-bold">{{ resultat()!.nombreCoursNonPlaces }}</div>
                    <div class="text-muted">Cours non placés</div>
                  </div>
                </div>
              </div>
              <div class="col-md-4">
                <div class="card text-center">
                  <div class="card-body">
                    <div class="display-6 text-primary fw-bold" style="font-size:1.4rem!important">{{ semaineInput }}</div>
                    <div class="text-muted">Semaine</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Sélecteur de classe obligatoire avant d'afficher la grille -->
            @if (!classeSelectionnee()) {
              <div class="card mb-4">
                <div class="card-body text-center py-5">
                  <i class="fas fa-chalkboard-teacher fa-3x text-muted mb-3 d-block"></i>
                  <h5 class="text-muted">Sélectionnez une classe pour afficher son emploi du temps</h5>
                  <div class="mt-3" style="max-width: 300px; margin: 0 auto;">
                    <select class="form-select" (change)="onClasseChange($event)">
                      <option value="">-- Choisir une classe --</option>
                      @for (c of classes(); track c.id) {
                        <option [value]="c.id">{{ c.nom }} ({{ c.code }})</option>
                      }
                    </select>
                  </div>
                </div>
              </div>
            }

            <!-- Calendrier — affiché uniquement si une classe est sélectionnée -->
            @if (classeSelectionnee()) {
              <div class="card mb-4">
                <div class="card-header d-flex justify-content-between align-items-center">
                  <h6 class="mb-0 text-white">
                    <i class="fas fa-calendar-alt me-2"></i>Emploi du temps —
                    <span class="ms-1 badge bg-light text-dark">{{ classeSelectionnee()!.nom }}</span>
                  </h6>
                  <button class="btn btn-sm btn-outline-light" (click)="classeSelectionnee.set(null)">
                    <i class="fas fa-times me-1"></i>Changer de classe
                  </button>
                </div>
                <div class="card-body p-2 calendrier">
                  @if (placementsFiltres().length === 0) {
                    <div class="text-center text-muted py-4">
                      <i class="fas fa-calendar-times fa-2x mb-2 d-block"></i>
                      Aucun cours planifié pour cette classe.
                    </div>
                  } @else {
                    <ng-container *ngTemplateOutlet="calendrierTpl; context: { placements: placementsFiltres() }"></ng-container>
                  }
                </div>
              </div>
            }

            <!-- Cours non placés -->
            @if ((resultat()!.coursNonPlaces || []).length > 0) {
              <div class="card">
                <div class="card-header">
                  <h6 class="mb-0 text-white"><i class="fas fa-exclamation-circle me-2 text-warning"></i>Cours non placés ({{ resultat()!.coursNonPlaces.length }})</h6>
                </div>
                <div class="card-body p-0">
                  <table class="table table-hover mb-0">
                    <thead><tr><th>Cours</th><th>Volume restant</th><th>Raison</th></tr></thead>
                    <tbody>
                      @for (c of resultat()!.coursNonPlaces; track c.coursId) {
                        <tr>
                          <td class="fw-semibold">{{ c.nomCours }}</td>
                          <td>{{ c.volumeRestant }}h</td>
                          <td class="text-muted small">{{ c.raison }}</td>
                        </tr>
                      }
                    </tbody>
                  </table>
                </div>
              </div>
            }
          }
        }

        <!-- === ONGLET HISTORIQUE === -->
        @if (onglet() === 'historique') {
          @if (historique().length === 0) {
            <div class="text-center text-muted py-5">
              <i class="fas fa-calendar-times fa-3x mb-3 d-block"></i>
              Aucun emploi du temps généré pour l'instant.
            </div>
          } @else {
            <div class="row">
              <!-- Liste des semaines -->
              <div class="col-md-3">
                <div class="card">
                  <div class="card-header"><h6 class="mb-0 text-white">Semaines générées</h6></div>
                  <div class="list-group list-group-flush">
                    @for (h of historique(); track h.semaine) {
                      <button class="list-group-item list-group-item-action historique-item"
                              [class.active]="semaineSelectionnee() === h.semaine"
                              (click)="selectionnerSemaine(h.semaine)">
                        <div class="d-flex justify-content-between align-items-center">
                          <span class="badge-semaine">{{ h.semaine }}</span>
                          <small class="text-muted">{{ h.resultat.nombrePlacements }} cours</small>
                        </div>
                        <small class="text-muted d-block mt-1">{{ h.date }}</small>
                      </button>
                    }
                  </div>
                </div>
              </div>

              <!-- Calendrier de la semaine sélectionnée -->
              <div class="col-md-9">
                @if (emploiSelectionne()) {
                  <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                      <h6 class="mb-0 text-white">
                        <i class="fas fa-calendar-alt me-2"></i>{{ emploiSelectionne()!.semaine }}
                      </h6>
                      <div class="d-flex gap-2 align-items-center">
                        <select class="form-select form-select-sm" style="width:auto" (change)="onClasseChange($event)">
                          <option [value]="''">Toutes les classes</option>
                          @for (c of classes(); track c.id) {
                            <option [value]="c.id">{{ c.nom }} ({{ c.code }})</option>
                          }
                        </select>
                        <button class="btn btn-sm btn-outline-danger" (click)="supprimerSemaineHistorique(emploiSelectionne()!.semaine)">
                          <i class="fas fa-trash me-1"></i>Supprimer
                        </button>
                      </div>
                    </div>
                    <div class="card-body p-2 calendrier">
                      @if (placementsHistoriqueFiltres().length === 0) {
                        <div class="text-center text-muted py-4">
                          <i class="fas fa-calendar-times fa-2x mb-2 d-block"></i>
                          Aucun cours planifié pour cette classe.
                        </div>
                      } @else {
                        <ng-container *ngTemplateOutlet="calendrierTpl; context: { placements: placementsHistoriqueFiltres() }"></ng-container>
                      }
                    </div>
                  </div>
                }
              </div>
            </div>
          }
        }

      </div>
    </div>

    <!-- Template réutilisable du calendrier -->
    <ng-template #calendrierTpl let-placements="placements">
      <table class="table-calendrier">
        <thead>
          <tr>
            <th style="width:80px">Heure</th>
            @for (j of jours; track j) { <th>{{ j }}</th> }
          </tr>
        </thead>
        <tbody>
          @for (creneau of creneaux; track creneau) {
            <tr>
              <td class="td-heure">{{ creneau }}<br><small>{{ heureFin(creneau) }}</small></td>
              @for (jour of jours; track jour) {
                <td>
                  @for (p of getPlacementsCase(placements, jour, creneau); track p.coursId) {
                    <div class="cours-card">
                      <div class="cours-nom">{{ p.nomCours }}</div>
                      <div class="cours-info">
                        <i class="fas fa-user me-1"></i>{{ getNomEnseignant(p.enseignantId) }}
                      </div>
                      <div class="cours-salle">
                        <i class="fas fa-door-open me-1"></i>{{ p.nomSalle }}
                      </div>
                    </div>
                  }
                </td>
              }
            </tr>
          }
        </tbody>
      </table>
    </ng-template>
  `
})
export class GenerationComponent implements OnInit {
  private planningService = inject(PlanningService);
  private coursClasseService = inject(CoursClasseService);
  private userService = inject(UserService);

  readonly jours = JOURS;
  readonly creneaux = CRENEAUX;

  semaineInput = '';
  onglet = signal<'generer' | 'historique'>('generer');
  loading = signal(false);
  erreur = signal<string | null>(null);
  resultat = signal<ResultatGeneration | null>(null);
  historique = signal<EmploiSauvegarde[]>(chargerHistorique(localStorage));
  semaineSelectionnee = signal<string | null>(null);
  classes = signal<Classe[]>([]);
  classeSelectionnee = signal<Classe | null>(null);
  enseignantsMap = signal<Map<number, string>>(new Map());

  emploiSelectionne = computed(() => {
    const s = this.semaineSelectionnee();
    return s ? this.historique().find(h => h.semaine === s) ?? null : null;
  });

  /** Placements filtrés par classe pour l'onglet Générer */
  placementsFiltres = computed<PlacementEffectue[]>(() => {
    const res = this.resultat();
    if (!res) return [];
    const classe = this.classeSelectionnee();
    return filtrerParClasse(res.placements, classe?.id ?? null);
  });

  /** Placements filtrés par classe pour l'onglet Historique */
  placementsHistoriqueFiltres = computed<PlacementEffectue[]>(() => {
    const emploi = this.emploiSelectionne();
    if (!emploi) return [];
    const classe = this.classeSelectionnee();
    return filtrerParClasse(emploi.resultat.placements, classe?.id ?? null);
  });

  ngOnInit(): void {
    this.coursClasseService.getAllClasses().subscribe({
      next: (classes) => this.classes.set(classes),
      error: () => this.classes.set([])
    });

    // Charger les enseignants pour afficher leur nom dans la grille
    this.userService.getEnseignants().subscribe({
      next: (enseignants) => {
        const map = new Map<number, string>();
        enseignants.forEach(e => map.set(e.id!, `${e.prenom ?? ''} ${e.nom ?? ''}`.trim()));
        this.enseignantsMap.set(map);
      },
      error: () => {}
    });

    // Charger les placements persistés en base pour l'affichage immédiat
    this.planningService.getEmploiDuTemps().subscribe({
      next: (placements) => {
        if (placements && placements.length > 0) {
          const resultatBase: ResultatGeneration = {
            nombrePlacements: placements.length,
            nombreCoursNonPlaces: 0,
            placements,
            coursNonPlaces: []
          };
          this.resultat.set(resultatBase);
        }
      },
      error: () => {}
    });
  }

  getNomEnseignant(enseignantId: number | null): string {
    if (!enseignantId) return 'N/A';
    return this.enseignantsMap().get(enseignantId) ?? `Ens. #${enseignantId}`;
  }

  onClasseChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    if (!value) {
      this.classeSelectionnee.set(null);
    } else {
      const id = Number(value);
      const found = this.classes().find(c => c.id === id) ?? null;
      this.classeSelectionnee.set(found);
    }
  }

  heureFin(heureDebut: string): string {
    const map: Record<string, string> = {
      '08:00': '10:00',
      '10:00': '12:00',
      '13:00': '15:00',
      '15:00': '17:00'
    };
    return map[heureDebut] ?? '';
  }

  getPlacementsCase(placements: PlacementEffectue[], jour: string, heureDebut: string): PlacementEffectue[] {
    return placements?.filter(p =>
      p.jour?.toUpperCase() === jour.toUpperCase() &&
      normaliserHeure(p.heureDebut ?? '') === heureDebut
    ) ?? [];
  }

  lancerGeneration(): void {
    this.loading.set(true);
    this.erreur.set(null);
    this.resultat.set(null);

    this.planningService.genererEmploiDuTemps().subscribe({
      next: (res) => {
        this.resultat.set(res);
        this.sauvegarderResultat(res);
        this.loading.set(false);
      },
      error: (err) => {
        this.erreur.set(typeof err.error === 'string' ? err.error : 'Erreur lors de la génération.');
        this.loading.set(false);
      }
    });
  }

  selectionnerSemaine(semaine: string): void {
    this.semaineSelectionnee.set(semaine);
  }

  supprimerSemaineHistorique(semaine: string): void {
    const updated = supprimerSemaine(semaine, localStorage);
    this.historique.set(updated);
    if (this.semaineSelectionnee() === semaine) {
      this.semaineSelectionnee.set(updated.length > 0 ? updated[0].semaine : null);
    }
  }

  private sauvegarderResultat(res: ResultatGeneration): void {
    const updated = sauvegarderResultat(this.semaineInput, res, localStorage);
    this.historique.set(updated);
  }
}
