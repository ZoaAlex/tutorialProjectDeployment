import { Component, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { User } from '../../models/user.model';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-utilisateurs',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, NavbarComponent],
  templateUrl: './utilisateurs.component.html'
})
export class UtilisateursComponent {
  userService = inject(UserService); // Inject service

  searchTerm = signal('');
  filterRole = signal('');
  showModal = signal(false);
  editMode = signal(false);

  users = signal<User[]>([]);

  currentUser = signal<Partial<User>>({ nom: '', prenom: '', email: '', role: 'admin', ecole: 'SJI' });

  constructor() {
    this.loadUsers();
  }

  loadUsers() {
    this.userService.getAllUsers().subscribe({
      next: (data) => this.users.set(data),
      error: (err) => console.error('Erreur chargement utilisateurs', err)
    });
  }

  filteredUsers = computed(() => {
    let result = this.users();
    if (this.searchTerm()) {
      const term = this.searchTerm().toLowerCase();
      result = result.filter(u => u.nom.toLowerCase().includes(term) || u.prenom.toLowerCase().includes(term) || u.email.toLowerCase().includes(term));
    }
    if (this.filterRole()) result = result.filter(u => u.role === this.filterRole());
    return result;
  });

  openModal(user?: User): void {
    this.editMode.set(!!user);
    this.currentUser.set(user ? { ...user } : { nom: '', prenom: '', email: '', role: 'admin', ecole: 'SJI' });
    this.showModal.set(true);
  }

  closeModal(): void { this.showModal.set(false); }

  saveUser(): void {
    const user = this.currentUser();
    // Prompt for password if creating new user
    if (!this.editMode() && !user.password) {
      const pwd = prompt('Entrez le mot de passe pour le nouvel utilisateur:');
      if (!pwd) return;
      user.password = pwd;
    }

    const payload = {
      ...user,
      role: user.role ? user.role.toUpperCase() : 'ROLE_ENSEIGNANT'
    };

    if (this.editMode() && user.id) {
      this.userService.updateUser(user.id, payload as any).subscribe({
        next: (updated) => {
          this.users.update(u => u.map(item => item.id === updated.id ? updated : item));
          this.closeModal();
        },
        error: (err) => console.error('Erreur modif', err)
      });
    } else {
      this.userService.createUser(payload as any).subscribe({
        next: (created) => {
          this.users.update(u => [...u, created]);
          this.closeModal();
        },
        error: (err) => console.error('Erreur création', err)
      });
    }
  }

  deleteUser(id: number): void {
    if (confirm('Supprimer cet utilisateur?')) {
      this.userService.deleteUser(id).subscribe({
        next: () => this.users.update(u => u.filter(item => item.id !== id)),
        error: (err) => console.error('Erreur suppression', err)
      });
    }
  }

  importLoading = signal(false);
  importSuccess = signal('');
  importError   = signal('');

  onImportExcel(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file  = input.files?.[0];
    if (!file) return;
    input.value = '';

    this.importLoading.set(true);
    this.importSuccess.set('');
    this.importError.set('');

    this.userService.importUsers(file).subscribe({
      next: (msg) => {
        this.importSuccess.set(msg);
        this.importLoading.set(false);
        this.loadUsers();
        setTimeout(() => this.importSuccess.set(''), 4000);
      },
      error: (err) => {
        this.importError.set(err.error ?? 'Erreur lors de l\'import');
        this.importLoading.set(false);
        setTimeout(() => this.importError.set(''), 6000);
      }
    });
  }

  updateUserStatus(id: number, user: User): void {
  // 1. Déterminer le nouveau statut
  const nouveauStatut = user.statut === 'ACTIF' ? 'INACTIF' : 'ACTIF';
  const action = nouveauStatut === 'ACTIF' ? 'activer' : 'désactiver';

  // 2. Confirmation
  if (confirm(`Voulez-vous ${action} cet utilisateur ?`)) {
    
    // 3. Créer un payload propre sans modifier l'objet d'origine
    const payload = { ...user,
      statut: nouveauStatut,
      role: user.role ? user.role.toUpperCase() : 'ROLE_ENSEIGNANT'
     };

    this.userService.updateUser(id, payload).subscribe({
      next: (updated) => {
        // 4. Mise à jour du Signal de manière immuable
        this.users.update(allUsers => 
          allUsers.map(u => u.id === id ? updated : u)
        );
      },
      error: (err) => {
        console.error('Erreur mise à jour statut', err);
        alert('La mise à jour a échoué.');
      }
    });
  }
}
}
