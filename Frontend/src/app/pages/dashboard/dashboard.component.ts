import { Component, computed, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin, catchError, of } from 'rxjs';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { SalleService } from '../../services/salle.service';
import { CoursClasseService } from '../../services/cours-classe.service';
import { PlanningService, PlacementEffectue } from '../../services/planning.service';

const JOURS_FR = ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'];

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, SidebarComponent, NavbarComponent],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  authService    = inject(AuthService);
  userService    = inject(UserService);
  salleService   = inject(SalleService);
  coursService   = inject(CoursClasseService);
  planningService = inject(PlanningService);

  loading = signal(true);

  stats = signal([
    { titre: 'Utilisateurs',  valeur: '…', icon: 'fa-users',          color: 'icon-blue'   },
    { titre: 'Cours',         valeur: '…', icon: 'fa-book',           color: 'icon-purple' },
    { titre: 'Salles',        valeur: '…', icon: 'fa-door-open',      color: 'icon-pink'   },
    { titre: 'Réservations',  valeur: '…', icon: 'fa-calendar-check', color: 'icon-green'  },
  ]);

  todayCours = signal<{
    id: number; intitule: string; code: string;
    heureDebut: string; heureFin: string; salleNom: string; type: string;
  }[]>([]);

  ngOnInit(): void {
    const todayFr = JOURS_FR[new Date().getDay()].toUpperCase();

    forkJoin({
      users:        this.userService.getAllUsers().pipe(catchError(() => of([]))),
      cours:        this.coursService.getAllCours().pipe(catchError(() => of([]))),
      salles:       this.salleService.getAllSalles().pipe(catchError(() => of([]))),
      reservations: this.salleService.getAllReservations().pipe(catchError(() => of([]))),
      placements:   this.planningService.getEmploiDuTemps().pipe(catchError(() => of([]))),
    }).subscribe(({ users, cours, salles, reservations, placements }) => {

      // ── Stats cards ──────────────────────────────────────────
      this.stats.set([
        { titre: 'Utilisateurs', valeur: String(users.length),        icon: 'fa-users',          color: 'icon-blue'   },
        { titre: 'Cours',        valeur: String(cours.length),        icon: 'fa-book',           color: 'icon-purple' },
        { titre: 'Salles',       valeur: String(salles.length),       icon: 'fa-door-open',      color: 'icon-pink'   },
        { titre: 'Réservations', valeur: String(reservations.length), icon: 'fa-calendar-check', color: 'icon-green'  },
      ]);

      // ── Cours du jour depuis les placements ──────────────────
      const aujourdhui: PlacementEffectue[] = placements.filter(
        (p: PlacementEffectue) => p.jour?.toUpperCase() === todayFr
      );

      // Enrichir avec le nom du cours depuis la liste cours
      this.todayCours.set(
        aujourdhui.map((p: PlacementEffectue) => {
          const coursObj = cours.find((c: any) => c.id === p.coursId);
          return {
            id:        p.coursId,
            intitule:  p.nomCours,
            code:      coursObj?.codeClasse ?? '—',
            heureDebut: (p.heureDebut ?? '').substring(0, 5),
            heureFin:   (p.heureFin   ?? '').substring(0, 5),
            salleNom:  p.nomSalle ?? '—',
            type:      coursObj?.type ?? '—',
          };
        }).sort((a, b) => a.heureDebut.localeCompare(b.heureDebut))
      );

      this.loading.set(false);
    });
  }
}
