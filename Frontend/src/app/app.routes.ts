import { Routes } from '@angular/router';
import { authGuard, adminGuard } from './guards/auth.guard';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { SallesComponent } from './pages/salles/salles.component';
import { CoursComponent } from './pages/cours/cours.component';
import { ReservationsComponent } from './pages/reservations/reservations.component';
import { UtilisateursComponent } from './pages/utilisateurs/utilisateurs.component';
import { MaterielComponent } from './pages/materiel/materiel.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'forgot-password', loadComponent: () => import('./pages/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent) },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', loadComponent: () => import('./pages/profile/profile.component').then(m => m.ProfileComponent), canActivate: [authGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'salles', component: SallesComponent, canActivate: [authGuard] },
  { path: 'cours', component: CoursComponent, canActivate: [authGuard] },
  { path: 'reservations', component: ReservationsComponent, canActivate: [authGuard] },
  { path: 'utilisateurs', component: UtilisateursComponent, canActivate: [authGuard, adminGuard] },
  { path: 'utilisateurs', component: UtilisateursComponent, canActivate: [authGuard, adminGuard] },
  { path: 'materiel', component: MaterielComponent, canActivate: [authGuard] },
  {
    path: 'disponibilites/enseignant',
    loadComponent: () => import('./pages/disponibilite-enseignant/disponibilite-enseignant.component').then(m => m.DisponibiliteEnseignantComponent),
    canActivate: [authGuard]
  },
  {
    path: 'disponibilites/admin',
    loadComponent: () => import('./pages/disponibilite-admin/disponibilite-admin.component').then(m => m.DisponibiliteAdminComponent),
    canActivate: [authGuard, adminGuard]
  },
  {
    path: 'demandes/enseignant',
    loadComponent: () => import('./pages/demandes-enseignant/demandes-enseignant.component').then(m => m.DemandesEnseignantComponent),
    canActivate: [authGuard]
  },
  {
    path: 'demandes/admin',
    loadComponent: () => import('./pages/demandes-admin/demandes-admin.component').then(m => m.DemandesAdminComponent),
    canActivate: [authGuard, adminGuard]
  },
  {
    path: 'generation',
    loadComponent: () => import('./pages/generation/generation.component').then(m => m.GenerationComponent),
    canActivate: [authGuard, adminGuard]
  },
  {
    path: 'emploi-du-temps',
    loadComponent: () => import('./pages/emploi-du-temps-editor/emploi-du-temps-editor.component').then(m => m.EmploiDuTempsEditorComponent),
    canActivate: [authGuard, adminGuard]
  },
  { path: '**', redirectTo: '/dashboard' }
];
