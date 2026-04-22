import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { SalleService } from '../../services/salle.service';
import { AuthService } from '../../services/auth.service';
import { Reservation, StatutReservation } from '../../models/reservation.model';
import { Salle } from '../../models/salle.model';

@Component({
  selector: 'app-reservations',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, NavbarComponent],
  templateUrl: './reservations.component.html'
})
export class ReservationsComponent implements OnInit {
  private salleService = inject(SalleService);
  authService = inject(AuthService);

  salles = signal<Salle[]>([]);
  reservations = signal<Reservation[]>([]);

  showModal = signal(false);
  filterStatut = signal('');

  newReservation = signal<Partial<Reservation>>({
    salleId: 0,
    dateDebut: new Date().toISOString().split('T')[0],
    dateFin: new Date().toISOString().split('T')[0],
    motif: '',
    statut: StatutReservation.EN_ATTENTE
  });

  filteredReservations = computed(() => {
    if (!this.filterStatut()) return this.reservations();
    return this.reservations().filter(r => r.statut === this.filterStatut());
  });

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.salleService.getAllSalles().subscribe(data => this.salles.set(data));
    this.salleService.getAllReservations().subscribe(data => this.reservations.set(data));
  }

  openModal(): void { this.showModal.set(true); }
  closeModal(): void { this.showModal.set(false); }

  submitReservation(): void {
    const res = {
      ...this.newReservation(),
      utilisateurId: this.authService.currentUser()?.id || 0,
      statut: StatutReservation.EN_ATTENTE
    } as Reservation;

    this.salleService.creerReservation(res).subscribe(() => {
      this.loadData();
      this.closeModal();
    });
  }

  cancelReservation(id: number): void {
    if (confirm('Annuler cette réservation?')) {
      // In a real app, we might have a specific cancel endpoint
      // for now we just delete or update if supported
      this.salleService.deleteReservation(id).subscribe(() => this.loadData());
    }
  }

  getSalleNom(id: number): string {
    return this.salles().find(s => s.id === id)?.nom || 'Inconnue';
  }
}
