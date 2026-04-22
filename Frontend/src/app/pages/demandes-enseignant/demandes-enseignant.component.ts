import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { SpecialEventService } from '../../services/special-event.service';
import { AuthService } from '../../services/auth.service';
import { DemandeEvent, StatutDemande } from '../../models/demande-event.model';

@Component({
  selector: 'app-demandes-enseignant',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, NavbarComponent],
  template: `
    <app-sidebar></app-sidebar>
    <app-navbar></app-navbar>
    
    <div class="main-content p-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 class="h3 mb-1">Mes Demandes d'Évènements</h2>
          <p class="text-muted">Gérez vos demandes de réservation pour des évènements spéciaux</p>
        </div>
        <button class="btn btn-primary" (click)="openModal()">
          <i class="fas fa-plus me-2"></i>Nouvelle Demande
        </button>
      </div>

      <!-- Filtres -->
      <div class="card border-0 shadow-sm mb-4">
        <div class="card-body">
          <div class="row g-3">
            <div class="col-md-8">
              <div class="input-group">
                <span class="input-group-text bg-transparent border-end-0">
                  <i class="fas fa-search text-muted"></i>
                </span>
                <input type="text" class="form-control border-start-0" 
                       placeholder="Rechercher une demande..." 
                       [(ngModel)]="searchTerm">
              </div>
            </div>
            <div class="col-md-4">
              <select class="form-select" [(ngModel)]="statusFilter">
                <option value="">Tous les statuts</option>
                <option [value]="StatutDemande.EN_ATTENTE">En attente</option>
                <option [value]="StatutDemande.VALIDE">Validée</option>
                <option [value]="StatutDemande.REFUSE">Refusée</option>
              </select>
            </div>
          </div>
        </div>
      </div>

      <!-- Liste des demandes -->
      <div class="row">
        @if (filteredDemandes().length > 0) {
          @for (demande of filteredDemandes(); track demande.id) {
            <div class="col-md-6 col-lg-4 mb-4">
              <div class="card h-100 border-0 shadow-sm hover-shadow transition-all">
                <div class="card-body p-4">
                  <div class="d-flex justify-content-between align-items-start mb-3">
                    <span class="badge rounded-pill" 
                          [class.bg-warning-subtle]="demande.status === StatutDemande.EN_ATTENTE"
                          [class.text-warning]="demande.status === StatutDemande.EN_ATTENTE"
                          [class.bg-success-subtle]="demande.status === StatutDemande.VALIDE"
                          [class.text-success]="demande.status === StatutDemande.VALIDE"
                          [class.bg-danger-subtle]="demande.status === StatutDemande.REFUSE"
                          [class.text-danger]="demande.status === StatutDemande.REFUSE">
                      {{ statusLabel(demande.status!) }}
                    </span>
                    @if (demande.status === StatutDemande.EN_ATTENTE) {
                      <div class="dropdown">
                        <button class="btn btn-link text-muted p-0" data-bs-toggle="dropdown">
                          <i class="fas fa-ellipsis-v"></i>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                          <li><a class="dropdown-menu-item py-2 px-3 d-block pointer" (click)="openModal(demande)">
                            <i class="fas fa-edit me-2 text-primary"></i>Modifier
                          </a></li>
                          <li><hr class="dropdown-divider"></li>
                          <li><a class="dropdown-menu-item py-2 px-3 d-block pointer text-danger" (click)="deleteDemande(demande.id!)">
                            <i class="fas fa-trash me-2"></i>Supprimer
                          </a></li>
                        </ul>
                      </div>
                    }
                  </div>
                  
                  <h5 class="card-title fw-bold mb-2">{{ demande.titre }}</h5>
                  <p class="card-text text-muted small mb-4 line-clamp-2">{{ demande.objectif }}</p>
                  
                  <div class="d-grid gap-2 mb-3">
                    <div class="d-flex align-items-center text-muted small">
                      <i class="far fa-calendar-alt me-2"></i>
                      <span>Du {{ demande.debutEvent | date:'dd/MM/yyyy HH:mm' }}</span>
                    </div>
                    <div class="d-flex align-items-center text-muted small">
                      <i class="far fa-calendar-alt me-2"></i>
                      <span>Au {{ demande.finEvent | date:'dd/MM/yyyy HH:mm' }}</span>
                    </div>
                    <div class="d-flex align-items-center text-muted small">
                      <i class="fas fa-users me-2"></i>
                      <span>{{ demande.nbreLimitParticipant }} participants max</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          }
        } @else {
          <div class="col-12 text-center py-5">
            <div class="mb-3 text-muted opacity-25">
              <i class="fas fa-folder-open fa-4x"></i>
            </div>
            <h4 class="text-muted">Aucune demande trouvée</h4>
            <p class="text-muted">Commencez par créer votre première demande d'évènement.</p>
          </div>
        }
      </div>
    </div>

    <!-- Modal Formulaire -->
    @if (showModal()) {
      <div class="modal fade show d-block" style="background: rgba(0,0,0,0.5);">
        <div class="modal-dialog modal-lg modal-dialog-centered">
          <div class="modal-content border-0 shadow-lg">
            <div class="modal-header border-bottom-0 pb-0">
              
              <button type="button" class="btn-close" (click)="closeModal()"></button>
            </div>
            <div class="modal-body p-4">
              <form (ngSubmit)="saveDemande()">
                <div class="row g-4">
                  <div class="col-12">
                    <label class="form-label fw-semibold">Titre de l'évènement</label>
                    <input type="text" class="form-control" [(ngModel)]="currentDemande().titre" name="titre" required placeholder="Ex: Conférence sur l'IA">
                  </div>
                  
                  <div class="col-12">
                    <label class="form-label fw-semibold">Objectif / Description</label>
                    <textarea class="form-control" [(ngModel)]="currentDemande().objectif" name="objectif" rows="3" required placeholder="Décrivez le but de l'évènement..."></textarea>
                  </div>
                  
                  <div class="col-md-6">
                    <label class="form-label fw-semibold">Début de l'évènement</label>
                    <input type="datetime-local" class="form-control" [(ngModel)]="currentDemande().debutEvent" name="debutEvent" required>
                  </div>
                  
                  <div class="col-md-6">
                    <label class="form-label fw-semibold">Fin de l'évènement</label>
                    <input type="datetime-local" class="form-control" [(ngModel)]="currentDemande().finEvent" name="finEvent" required>
                  </div>
                  
                  <div class="col-md-12">
                    <label class="form-label fw-semibold">Nombre maximum de participants</label>
                    <div class="input-group">
                      <span class="input-group-text bg-light"><i class="fas fa-users"></i></span>
                      <input type="number" class="form-control" [(ngModel)]="currentDemande().nbreLimitParticipant" name="nbreLimitParticipant" required min="1">
                    </div>
                  </div>
                </div>
                
                <div class="modal-footer border-top-0 px-0 pb-0 mt-4">
                  <button type="button" class="btn btn-light px-4" (click)="closeModal()">Annuler</button>
                  <button type="submit" class="btn btn-primary px-4">
                    {{ editMode() ? 'Mettre à jour' : 'Soumettre la demande' }}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    }
  `,
  styles: `
    .main-content { margin-left: 260px; min-height: 100vh; background-color: #f8f9fa; }
    .hover-shadow:hover { transform: translateY(-5px); cursor: default; }
    .transition-all { transition: all 0.3s ease; }
    .pointer { cursor: pointer; }
    .dropdown-menu-item:hover { background-color: #f8f9fa; text-decoration: none; }
    .line-clamp-2 { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
    
    .bg-warning-subtle { background-color: #fff3cd !important; }
    .bg-success-subtle { background-color: #d1e7dd !important; }
    .bg-danger-subtle { background-color: #f8d7da !important; }
  `
})
export class DemandesEnseignantComponent implements OnInit {
  private eventService = inject(SpecialEventService);
  private authService = inject(AuthService);

  StatutDemande = StatutDemande;

  demandes = signal<DemandeEvent[]>([]);
  searchTerm = '';
  statusFilter = '';

  showModal = signal(false);
  editMode = signal(false);
  currentDemande = signal<DemandeEvent>(this.initEmptyDemande());

  filteredDemandes = computed(() => {
    return this.demandes().filter(d => {
      const matchesSearch = d.titre.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        d.objectif.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchesStatus = !this.statusFilter || d.status === this.statusFilter;
      return matchesSearch && matchesStatus;
    });
  });

  ngOnInit() {
    this.loadDemandes();
  }

  loadDemandes() {
    const user = this.authService.currentUser();
    if (user?.id) {
      this.eventService.getDemandesByEnseignant(user.id).subscribe({
        next: (data) => {
          // Si le backend filtre déjà par prof, on prend tout.
          // Sinon on filtre ici par sécurité.
          this.demandes.set(data.filter(d => d.enseignantId === user.id));
        },
        error: (err) => console.error('Erreur chargement demandes', err)
      });
    }
  }

  initEmptyDemande(): DemandeEvent {
    return {
      titre: '',
      objectif: '',
      debutEvent: '',
      finEvent: '',
      nbreLimitParticipant: 20,
      enseignantId: this.authService.currentUser()?.id || 0
    };
  }

  openModal(demande?: DemandeEvent) {
    this.editMode.set(!!demande);
    if (demande) {
      // Format dates for datetime-local input
      const d = { ...demande };
      if (d.debutEvent) d.debutEvent = this.formatDateTime(new Date(d.debutEvent));
      if (d.finEvent) d.finEvent = this.formatDateTime(new Date(d.finEvent));
      this.currentDemande.set(d);
    } else {
      this.currentDemande.set(this.initEmptyDemande());
    }
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
  }

  saveDemande() {
    const demande = this.currentDemande();
    if (this.editMode() && demande.id) {
      this.eventService.updateDemande(demande.id, demande).subscribe({
        next: () => {
          this.loadDemandes();
          this.closeModal();
        },
        error: (err) => alert('Erreur lors de la modification')
      });
    } else {
      this.eventService.createDemande(demande).subscribe({
        next: () => {
          this.loadDemandes();
          this.closeModal();
        },
        error: (err) => alert('Erreur lors de la création')
      });
    }
  }

  deleteDemande(id: number) {
    if (confirm('Voulez-vous vraiment supprimer cette demande ?')) {
      this.eventService.deleteDemande(id).subscribe({
        next: () => this.loadDemandes(),
        error: (err) => alert('Erreur lors de la suppression')
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

  private formatDateTime(date: Date): string {
    const pad = (n: number) => n < 10 ? '0' + n : n;
    return date.getFullYear() + '-' +
      pad(date.getMonth() + 1) + '-' +
      pad(date.getDate()) + 'T' +
      pad(date.getHours()) + ':' +
      pad(date.getMinutes());
  }
}
