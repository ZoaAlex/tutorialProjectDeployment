import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  user = { nom: '', prenom: '', email: '', password: '', role: 'enseignant' as const, ecole: 'SJI' };
  error = signal('');
  success = signal('');
  loading = signal(false);

  onSubmit(): void {
    this.error.set('');
    this.success.set('');
    this.loading.set(true);

    setTimeout(() => {
      this.success.set('Compte créé avec succès! Vous pouvez maintenant vous connecter.');
      this.loading.set(false);
    }, 500);
  }
}
