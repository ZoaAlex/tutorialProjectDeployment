import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './forgot-password.component.html',
})
export class ForgotPasswordComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  step = signal(1); // 1: Email, 2: Code, 3: New Password
  email = '';
  code = '';
  newPassword = '';
  confirmPassword = '';

  error = signal('');
  message = signal('');
  loading = signal(false);

  sendCode(): void {
    if (!this.email) {
      this.error.set('Veuillez entrer votre email');
      return;
    }
    this.loading.set(true);
    this.error.set('');
    this.authService.forgotPassword(this.email).subscribe({
      next: () => {
        this.message.set('Un code de vérification a été envoyé à votre adresse email.');
        this.step.set(2);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.error || 'Erreur lors de l\'envoi du code');
        this.loading.set(false);
      }
    });
  }

  verifyCode(): void {
    if (!this.code) {
      this.error.set('Veuillez entrer le code reçu');
      return;
    }
    this.loading.set(true);
    this.error.set('');
    this.authService.verifyCode(this.email, this.code).subscribe({
      next: () => {
        this.step.set(3);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Code invalide ou expiré');
        this.loading.set(false);
      }
    });
  }

  resetPassword(): void {
    if (this.newPassword !== this.confirmPassword) {
      this.error.set('Les mots de passe ne correspondent pas');
      return;
    }
    this.loading.set(true);
    this.error.set('');
    this.authService.resetPassword({
      email: this.email,
      code: this.code,
      newPassword: this.newPassword
    }).subscribe({
      next: () => {
        this.message.set('Mot de passe réinitialisé avec succès !');
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (err) => {
        this.error.set('Erreur lors de la réinitialisation');
        this.loading.set(false);
      }
    });
  }
}
