import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [CommonModule, FormsModule, SidebarComponent, NavbarComponent],
    templateUrl: './profile.component.html',
})
export class ProfileComponent implements OnInit {
    private authService = inject(AuthService);
    private userService = inject(UserService);

    user = signal<User | null>(null);
    editMode = signal(false);
    loading = signal(false);
    saving = signal(false);
    message = signal('');
    error = signal('');

    // Editable fields
    formData: Partial<User> = {};

    // Password change fields
    passwordData = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
    };
    passwordMessage = signal('');
    passwordError = signal('');
    changingPassword = signal(false);

    ngOnInit() {
        this.loadProfile();
    }

    loadProfile() {
        const currentUser = this.authService.currentUser();
        if (currentUser) {
            this.loading.set(true);
            this.userService.getById(currentUser.id).subscribe({
                next: (user) => {
                    this.user.set(user);
                    this.formData = { ...user };
                    this.loading.set(false);
                },
                error: (err) => {
                    this.error.set('Erreur lors du chargement du profil');
                    this.loading.set(false);
                }
            });
        }
    }

    toggleEdit() {
        if (this.editMode()) {
            // Cancel edit -> restore from current user signal
            this.formData = { ...this.user()! };
        }
        this.editMode.set(!this.editMode());
        this.message.set('');
        this.error.set('');
    }

    saveProfile() {
        if (!this.user()) return;
        this.saving.set(true);
        this.message.set('');
        this.error.set('');

        const userId = this.user()!.id;
        this.userService.updateUser(userId, this.formData).subscribe({
            next: (updatedUser: User) => {
                this.user.set(updatedUser);
                this.editMode.set(false);
                this.saving.set(false);
                this.message.set('Profil mis à jour avec succès !');
            },
            error: (err: any) => {
                this.error.set('Erreur lors de la mise à jour du profil');
                this.saving.set(false);
            }
        });
    }


    onChangePassword() {
        if (this.passwordData.newPassword !== this.passwordData.confirmPassword) {
            this.passwordError.set('Les nouveaux mots de passe ne correspondent pas');
            return;
        }

        this.changingPassword.set(true);
        this.passwordMessage.set('');
        this.passwordError.set('');

        const email = this.user()!.email;
        this.authService.changePassword(email, {
            oldPassword: this.passwordData.oldPassword,
            newPassword: this.passwordData.newPassword
        }).subscribe({
            next: (msg: string) => {
                this.passwordMessage.set(msg);
                this.passwordData = { oldPassword: '', newPassword: '', confirmPassword: '' };
                this.changingPassword.set(false);
            },
            error: (err: any) => {
                this.passwordError.set(err.error || 'Erreur lors du changement de mot de passe');
                this.changingPassword.set(false);
            }
        });
    }
}


