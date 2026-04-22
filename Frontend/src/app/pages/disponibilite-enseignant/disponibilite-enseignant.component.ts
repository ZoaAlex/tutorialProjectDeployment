import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PlanningService } from '../../services/planning.service';
import { AuthService } from '../../services/auth.service';
import { Disponibilite } from '../../models/disponibilite.model';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';

@Component({
  selector: 'app-disponibilite-enseignant',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, SidebarComponent],
  template: `
    <app-sidebar></app-sidebar>
    <div class="main-content">
      <app-navbar pageTitle="Mes Disponibilités"></app-navbar>

      <div class="content-wrapper">
        <div class="card mb-4">
          <div class="card-body d-flex justify-content-between align-items-center">
            <h5 class="mb-0 text-white">Gérer mes créneaux</h5>
            <button class="btn btn-primary" (click)="openModal()">
              <i class="fas fa-plus me-2"></i>Ajouter une disponibilité
            </button>
          </div>
        </div>

        <div class="card">
          <div class="card-body p-0">
             <div class="table-responsive">
              <table class="table table-schedule mb-0">
                <thead>
                  <tr>
                    <th class="time-slot">Heure</th>
                    @for (jour of jours; track jour) {
                      <th>{{ jour }}</th>
                    }
                  </tr>
                </thead>
                <tbody>
                  @for (heureRange of heures; track heureRange) {
                    <tr>
                      <td class="time-slot">{{ heureRange }}</td>
                      @for (jour of jours; track jour) {
                        @if (getDispoForSlot(jour, getStartHour(heureRange)); as dispo) {
                          <td class="course-cell" (click)="editDispo(dispo)">
                            <div class="course-block" style="background: var(--gradient-green); cursor: pointer; position: relative; overflow: hidden;">
                              <div class="course-name">Disponible</div>
                              <div class="course-info">{{ dispo.jour }} {{ dispo.heureDebut }} - {{ dispo.heureFin }}</div>
                              <div style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; pointer-events: none;">
                                <i class="fas fa-times" style="color: rgba(220, 53, 69, 0.8); font-size: 3.5rem;"></i>
                              </div>
                            </div>
                          </td>
                        } @else {
                          <td (click)="openModal(jour, getStartHour(heureRange))" style="cursor: pointer;" title="Cliquez pour ajouter"></td>
                        }
                      }
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- MODAL -->
    @if (showModal()) {
      <div class="modal fade show d-block" style="background: rgba(0,0,0,0.5)">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">{{ isEditing() ? 'Modifier' : 'Ajouter' }} une disponibilité</h5>
              <button type="button" class="btn-close" (click)="closeModal()"></button>
            </div>
            <div class="modal-body">
              <form (ngSubmit)="saveDispo()">
                <div class="mb-3">
                  <label class="form-label">Jour</label>
                  <select class="form-select" [(ngModel)]="currentDispo.jour" name="jour" required>
                    @for (j of jours; track j) { <option [value]="j">{{ j }}</option> }
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Type de disponibilité</label>
                  <select class="form-select" [(ngModel)]="currentDispo.type" name="type" required>
                    <option value="MATIN">Matin (08:00-12:00)</option>
                    <option value="APRES_MIDI">Après-midi (13:00-17:00)</option>
                    <option value="SOIR">Soir (18:00-22:00)</option>
                    <option value="JOURNEE_COMPLETE">Journée complète</option>
                    <option value="PERSONNALISE">Personnalisé</option>
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Commentaire</label>
                  <textarea class="form-control" [(ngModel)]="currentDispo.commentaire" name="commentaire" rows="2" placeholder="Facultatif..."></textarea>
                </div>
                <div class="row g-2">
                  <div class="col-6">
                    <label class="form-label">Heure Début</label>
                    <select class="form-select" [(ngModel)]="currentDispo.heureDebut" name="heureDebut" required>
                      @for (h of exclusiveHeures; track h) { <option [value]="h">{{ h }}</option> }
                    </select>
                  </div>
                  <div class="col-6">
                    <label class="form-label">Heure Fin</label>
                     <select class="form-select" [(ngModel)]="currentDispo.heureFin" name="heureFin" required>
                      @for (h of exclusiveHeures; track h) { <option [value]="h">{{ h }}</option> }
                    </select>
                  </div>
                </div>
                
                <div class="mt-4 d-flex justify-content-between">
                  @if (isEditing()) {
                    <button type="button" class="btn btn-danger" (click)="deleteDispo()">Supprimer</button>
                  } @else {
                    <div></div>
                  }
                  <div class="d-flex gap-2">
                    <button type="button" class="btn btn-glass" (click)="closeModal()">Annuler</button>
                    <button type="submit" class="btn btn-success">Enregistrer</button>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    }
  `
})
export class DisponibiliteEnseignantComponent implements OnInit {
  planningService = inject(PlanningService);
  authService = inject(AuthService);

  disponibilites = signal<Disponibilite[]>([]);
  showModal = signal(false);
  isEditing = signal(false);

  jours = ['Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'];
  // Créneaux de 2h alignés sur le moteur de génération
  heures = [
    '08:00 - 10:00', '10:00 - 12:00',
    '13:00 - 15:00', '15:00 - 17:00'
  ];

  // Heures alignées exactement sur les créneaux de 2h du moteur de génération
  // Matin : 08h-10h, 10h-12h | Après-midi : 13h-15h, 15h-17h
  exclusiveHeures = ['08:00', '10:00', '12:00', '13:00', '15:00', '17:00'];

  currentDispo: any = { jour: 'Lundi', type: 'MATIN', heureDebut: '08:00', heureFin: '12:00' };

  ngOnInit() {
    this.loadDisponibilites();
  }

  loadDisponibilites() {
    const user = this.authService.currentUser();
    if (user && user.id) {
      this.planningService.getEnseignantDisponibilites(user.id).subscribe({
        next: (data) => {
          // Normalize data if necessary (e.g. UPPERCASE jours from backend to Title Case)
          // For now assuming backend returns exactly what we need or we map.
          // If backend returns "LUNDI", we might want to map to "Lundi" for display or comparison.
          const normalizedData = data.map(d => ({
            ...d,
            jour: this.capitalize(d.jour)
          }));
          this.disponibilites.set(normalizedData);
        },
        error: (err) => console.error('Erreur chargement dispos', err)
      });
    }
  }

  getDispoForSlot(jour: string, heure: string): Disponibilite | undefined {
    return this.disponibilites().find(d => {
      if (d.jour !== jour) return false;
      // Normaliser sur 5 chars (le backend peut retourner "08:00:00")
      const start = (d.heureDebut ?? '').substring(0, 5);
      const end = (d.heureFin ?? '').substring(0, 5);
      return heure >= start && heure < end;
    });
  }

  openModal(jour?: string, heure?: string) {
    this.isEditing.set(false);
    this.currentDispo = {
      jour: jour || 'Lundi',
      type: 'MATIN',
      heureDebut: heure || '08:00',
      heureFin: this.getNextHour(heure || '11:00')
    };
    this.showModal.set(true);
  }

  editDispo(dispo: Disponibilite) {
    this.isEditing.set(true);
    this.currentDispo = { ...dispo }; // clone
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
  }

  saveDispo() {
    const user = this.authService.currentUser();
    if (!user) return;

    // Prepare payload (convert jour to UPPERCASE for backend)
    const payload = {
      ...this.currentDispo,
      enseignantId: user.id,
      jour: this.currentDispo.jour.toUpperCase(),
      // Ensure time format HH:mm:ss if needed. Backend likely tolerates HH:mm but let's see.
      heureDebut: this.ensureTimeFormat(this.currentDispo.heureDebut),
      heureFin: this.ensureTimeFormat(this.currentDispo.heureFin)
    };

    if (this.isEditing()) {
      this.planningService.updateDisponibilite(payload.id, payload).subscribe(() => {
        this.loadDisponibilites();
        this.closeModal();
      });
    } else {
      this.planningService.createDisponibilite(payload).subscribe(() => {
        this.loadDisponibilites();
        this.closeModal();
      });
    }
  }

  deleteDispo() {
    if (confirm('Supprimer ce créneau ?')) {
      this.planningService.deleteDisponibilite(this.currentDispo.id).subscribe(() => {
        this.loadDisponibilites();
        this.closeModal();
      });
    }
  }

  getNextHour(heure: string): string {
    const [h, m] = heure.split(':').map(Number);
    const nextH = h + 1;
    return `${nextH.toString().padStart(2, '0')}:00`;
  }

  capitalize(s: string): string {
    if (!s) return '';
    return s.charAt(0).toUpperCase() + s.slice(1).toLowerCase();
  }

  ensureTimeFormat(time: string): string {
    if (time.length === 5) return time + ':00';
    return time;
  }

  getStartHour(range: string): string {
    return range.split(' - ')[0];
  }
}
