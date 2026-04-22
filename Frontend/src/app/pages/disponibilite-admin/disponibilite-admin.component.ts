import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PlanningService } from '../../services/planning.service';
import { UserService } from '../../services/user.service';
import { Disponibilite } from '../../models/disponibilite.model';
import { User } from '../../models/user.model';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';

@Component({
  selector: 'app-disponibilite-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, SidebarComponent],
  template: `
    <app-sidebar></app-sidebar>
    <div class="main-content">
      <app-navbar pageTitle="Disponibilités Enseignants (Admin)"></app-navbar>

      <div class="content-wrapper">
        <div class="card mb-4">
          <div class="card-body">
            <h5 class="text-white mb-3">Sélectionner un enseignant</h5>
             <select class="form-select" [ngModel]="selectedEnseignantId()" (ngModelChange)="onEnseignantSelect($event)">
               <option [ngValue]="null" disabled selected>-- Choisir un enseignant --</option>
               @for (ens of enseignants(); track ens.id) {
                 <option [value]="ens.id">{{ ens.nom }} {{ ens.prenom }}</option>
               }
             </select>
          </div>
        </div>

        @if (selectedEnseignantId()) {
          <div class="card animate-fade-in-up">
             <div class="card-header d-flex justify-content-between align-items-center">
                <span>Planning de {{ getSelectedEnseignantName() }}</span>
             </div>
            <div class="card-body p-0">
               <div class="table-responsive">
                <table class="table table-schedule mb-0">
                  <thead>
                    <tr>
                      <th class="time-slot">Heure</th>
                      @for (jour of jours; track jour) { <th>{{ jour }}</th> }
                    </tr>
                  </thead>
                  <tbody>
                    @for (heureRange of heures; track heureRange) {
                      <tr>
                        <td class="time-slot">{{ heureRange }}</td>
                        @for (jour of jours; track jour) {
                          @if (getDispoForSlot(jour, getStartHour(heureRange)); as dispo) {
                            <td class="course-cell">
                              <div class="course-block" style="background: var(--gradient-blue); position: relative; overflow: hidden;">
                              
                                <div style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; pointer-events: none;">
                                  <i class="fas fa-times" style="color: rgba(220, 53, 69, 0.8); font-size: 3.5rem;"></i>
                                </div>
                              </div>
                            </td>
                          } @else {
                            <td></td>
                          }
                        }
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        }
      </div>
    </div>
  `
})
export class DisponibiliteAdminComponent implements OnInit {
  planningService = inject(PlanningService);
  userService = inject(UserService);

  enseignants = signal<User[]>([]);
  selectedEnseignantId = signal<number | null>(null);
  disponibilites = signal<Disponibilite[]>([]);

  jours = ['Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'];
  // Créneaux de 2h alignés sur le moteur de génération (8h-10h, 10h-12h, 13h-15h, 15h-17h)
  heures = [
    '08:00 - 10:00', '10:00 - 12:00',
    '13:00 - 15:00', '15:00 - 17:00'
  ];

  ngOnInit() {
    this.loadEnseignants();
  }

  loadEnseignants() {
    this.userService.getEnseignants().subscribe({
      next: (data) => this.enseignants.set(data),
      error: (err) => console.error('Erreur chargement enseignants', err)
    });
  }

  onEnseignantSelect(idStr: string | number) {
    const id = Number(idStr);
    this.selectedEnseignantId.set(id);
    this.planningService.getEnseignantDisponibilites(id).subscribe({
      next: (data) => {
        const normalizedData = data.map(d => ({
          ...d,
          jour: this.capitalize(d.jour)
        }));
        this.disponibilites.set(normalizedData);
      },
      error: (err) => console.error('Erreur chargement dispos admin', err)
    });
  }

  getDispoForSlot(jour: string, heure: string): Disponibilite | undefined {
    return this.disponibilites().find(d => {
      if (d.jour !== jour) return false;
      // Normaliser sur 5 chars (le backend peut retourner "08:00:00")
      const start = (d.heureDebut ?? '').substring(0, 5);
      const end = (d.heureFin ?? '').substring(0, 5);
      // Le créneau est couvert si la dispo englobe au moins le début du créneau
      return heure >= start && heure < end;
    });
  }

  getSelectedEnseignantName(): string {
    const ens = this.enseignants().find(e => e.id === Number(this.selectedEnseignantId()));
    return ens ? `${ens.nom} ${ens.prenom}` : '';
  }

  capitalize(s: string): string {
    if (!s) return '';
    return s.charAt(0).toUpperCase() + s.slice(1).toLowerCase();
  }

  getStartHour(range: string): string {
    return range.split(' - ')[0];
  }
}
