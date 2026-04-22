import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { SpecialEventService } from '../../services/special-event.service';
import { DemandeEvent, StatutDemande } from '../../models/demande-event.model';

@Component({
  selector: 'app-demandes-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, NavbarComponent],
  template: `
    <app-sidebar></app-sidebar>
    <app-navbar></app-navbar>
    
    <div class="main-content p-4">
      <div class="mb-4">
        <h2 class="h3 mb-1">Validation des Évènements</h2>
        <p class="text-muted">Gérez et validez les demandes d'évènements spéciaux soumises par les enseignants</p>
      </div>

      <!-- Filtres et Stats -->
      <div class="row g-4 mb-4">
        <div class="col-md-3">
          <div class="card border-0 shadow-sm bg-primary text-white">
            <div class="card-body p-3">
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <h6 class="mb-1 opacity-75">En attente</h6>
                  <h3 class="mb-0 fw-bold">{{ stats().pending }}</h3>
                </div>
                <i class="fas fa-hourglass-half fa-2x opacity-25"></i>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-9">
          <div class="card border-0 shadow-sm">
            <div class="card-body">
              <div class="row g-3">
                <div class="col-md-8">
                  <div class="input-group">
                    <span class="input-group-text bg-transparent border-end-0">
                      <i class="fas fa-search text-muted"></i>
                    </span>
                    <input type="text" class="form-control border-start-0" 
                           placeholder="Rechercher par titre ou enseignant..." 
                           [(ngModel)]="searchTerm">
                  </div>
                </div>
                <div class="col-md-4">
                  <select class="form-select" [(ngModel)]="statusFilter">
                    <option value="">Tous les statuts</option>
                    <option [value]="StatutDemande.EN_ATTENTE">En attente</option>
                    <option [value]="StatutDemande.VALIDE">Validées</option>
                    <option [value]="StatutDemande.REFUSE">Refusées</option>
                  </select>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Table des demandes -->
      <div class="card border-0 shadow-sm overflow-hidden">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="bg-light">
              <tr>
                <th class="ps-4">Évènement</th>
                <th>Enseignant</th>
                <th>Date Prévue</th>
                <th>Participants</th>
                <th>Statut</th>
                <th class="text-end pe-4">Actions</th>
              </tr>
            </thead>
            <tbody>
              @for (demande of filteredDemandes(); track demande.id) {
                <tr>
                  <td class="ps-4">
                    <div class="fw-bold text-dark">{{ demande.titre }}</div>
                    <div class="text-muted small text-truncate" style="max-width: 250px;">{{ demande.objectif }}</div>
                  </td>
                  <td>
                    <div class="d-flex align-items-center">
                      <div class="avatar-sm bg-info-subtle text-info rounded-circle me-2 d-flex align-items-center justify-content-center" style="width: 32px; height: 32px;">
                        {{ demande.enseignantId }}
                      </div>
                      <span class="small font-monospace">ID: {{ demande.enseignantId }}</span>
                    </div>
                  </td>
                  <td>
                    <div class="small">{{ demande.debutEvent | date:'dd MMM yyyy' }}</div>
                    <div class="text-muted smaller">{{ demande.debutEvent | date:'HH:mm' }} - {{ demande.finEvent | date:'HH:mm' }}</div>
                  </td>
                  <td>
                    <span class="badge bg-light text-dark border">
                      <i class="fas fa-users me-1 text-muted"></i>{{ demande.nbreLimitParticipant }}
                    </span>
                  </td>
                  <td>
                    <span class="badge rounded-pill" 
                          [class.bg-warning-subtle]="demande.status === StatutDemande.EN_ATTENTE"
                          [class.text-warning]="demande.status === StatutDemande.EN_ATTENTE"
                          [class.bg-success-subtle]="demande.status === StatutDemande.VALIDE"
                          [class.text-success]="demande.status === StatutDemande.VALIDE"
                          [class.bg-danger-subtle]="demande.status === StatutDemande.REFUSE"
                          [class.text-danger]="demande.status === StatutDemande.REFUSE">
                      {{ statusLabel(demande.status!) }}
                    </span>
                  </td>
                  <td class="text-end pe-4">
                    @if (demande.status === StatutDemande.EN_ATTENTE) {
                      <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-success" (click)="VALIDATEStatus(demande.id!, StatutDemande.VALIDE)" title="Valider">
                          <i class="fas fa-check"></i>
                        </button>
                        <button class="btn btn-outline-danger" (click)="REFUSEStatus(demande.id!, StatutDemande.REFUSE)" title="Refuser">
                          <i class="fas fa-times"></i>
                        </button>
                      </div>
                    }
                  </td>
                </tr>
              } @empty {
                <tr>
                  <td colspan="6" class="text-center py-5 text-muted">
                    <i class="fas fa-inbox fa-3x mb-3 opacity-25"></i>
                    <p>Aucune demande à traiter pour le moment.</p>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
  styles: `
    .main-content { margin-left: 260px; min-height: 100vh; background-color: #f8f9fa; }
    .bg-warning-subtle { background-color: #fff3cd !important; }
    .bg-success-subtle { background-color: #d1e7dd !important; }
    .bg-danger-subtle { background-color: #f8d7da !important; }
    .smaller { font-size: 0.75rem; }
  `
})
export class DemandesAdminComponent implements OnInit {
  private eventService = inject(SpecialEventService);

  StatutDemande = StatutDemande;
  demandes = signal<DemandeEvent[]>([]);
  searchTerm = '';
  statusFilter = '';

  stats = computed(() => {
    const list = this.demandes();
    return {
      pending: list.filter(d => d.status === StatutDemande.EN_ATTENTE).length,
      validated: list.filter(d => d.status === StatutDemande.VALIDE).length,
      refused: list.filter(d => d.status === StatutDemande.REFUSE).length
    };
  });

  filteredDemandes = computed(() => {
    return this.demandes().filter(d => {
      const matchesSearch = d.titre.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchesStatus = !this.statusFilter || d.status === this.statusFilter;
      return matchesSearch && matchesStatus;
    });
  });

  ngOnInit() {
    this.loadDemandes();
  }

  loadDemandes() {
    this.eventService.getAllDemandes().subscribe({
      next: (data) => this.demandes.set(data),
      error: (err) => console.error('Erreur chargement demandes', err)
    });
  }

  VALIDATEStatus(id: number, status: StatutDemande) {
    const msg = status === StatutDemande.VALIDE ? 'Valider cette demande ?' :
      status === StatutDemande.REFUSE ? 'Refuser cette demande ?' :
        'Remettre en attente cette demande ?';

    if (confirm(msg)) {
      this.eventService.validateDemande(id).subscribe({
        next: () => this.loadDemandes(),
        error: (err) => alert('Erreur lors de la mise à jour du statut')
      });
    }
  }
  REFUSEStatus(id: number, status: StatutDemande) {
    const msg = status === StatutDemande.VALIDE ? 'Valider cette demande ?' :
      status === StatutDemande.REFUSE ? 'Refuser cette demande ?' :
        'Remettre en attente cette demande ?';

    if (confirm(msg)) {
      this.eventService.refuseDemande(id).subscribe({
        next: () => this.loadDemandes(),
        error: (err) => alert('Erreur lors de la mise à jour du statut')
      });
    }
  }

  statusLabel(status: StatutDemande): string {
    switch (status) {
      case StatutDemande.EN_ATTENTE: return 'En attente';
      case StatutDemande.VALIDE: return 'Validée';
      case StatutDemande.REFUSE: return 'Refusée';
      default: return status;
    }
  }
}
