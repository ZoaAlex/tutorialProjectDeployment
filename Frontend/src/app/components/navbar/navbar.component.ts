import { Component, Input, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { ThemeService } from '../../services/theme.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent {
  @Input() pageTitle = 'Dashboard';

  private authService = inject(AuthService);
  themeService = inject(ThemeService);


  userName = computed(() => {
    const user = this.authService.currentUser();
    return user ? `${user.prenom} ${user.nom}` : '';
  });

  userInitials = computed(() => {
    const user = this.authService.currentUser();
    return user ? `${user.prenom[0]}${user.nom[0]}` : '';
  });

  userRole = computed(() => {
    const user = this.authService.currentUser();
    if (!user) return '';
    const roles: Record<string, string> = { admin: 'Administrateur', enseignant: 'Enseignant' };
    return roles[user.role] || user.role;
  });

  toggleSidebar(): void {
    document.querySelector('.sidebar')?.classList.toggle('show');
  }


  logout(): void {
    this.authService.logout();
  }
}
