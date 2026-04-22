import { Injectable, signal, computed, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { User, LoginRequest } from '../models/user.model';
import { environment } from '../../environments/environment';
import { tap, switchMap, catchError } from 'rxjs/operators';
import { Observable, of, throwError } from 'rxjs';

interface LoginResponse {
  token: string;
  changePasswordRequired: boolean;
  role: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private currentUserSignal = signal<User | null>(null);
  currentUser = this.currentUserSignal.asReadonly();
  isAuthenticated = computed(() => !!this.currentUserSignal());

  // Helper computed properties
  isAdmin = computed(() => this.hasRole('admin'));
  isEnseignant = computed(() => this.hasRole('enseignant'));

  constructor() {
    this.restoreSession();
  }

  private restoreSession(): void {
    const storedUser = localStorage.getItem('currentUser');
    const token = localStorage.getItem('token');
    if (storedUser && token) {
      this.currentUserSignal.set(JSON.parse(storedUser));
    }
  }

  login(credentials: LoginRequest): Observable<User> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, credentials).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
      }),
      switchMap(() => {
        // Fetch full user profile using the email from credentials
        // Note: In a real app, we might decode the token to get the email/sub
        return this.http.get<User>(`${environment.apiUrl}/users/by-email`, {
          params: { email: credentials.email }
        });
      }),
      tap(user => {
        // Map backend roles (e.g. ['ROLE_ADMIN']) to frontend expectation if needed
        // Assuming backend returns roles in the User object or we stick with the ones from LoginResponse
        // Let's assume User object from backend has proper mapping or we use it as is.
        // If User model has 'role' string and backend has 'roles' array, we might need adjustments.
        // For now, storing as is.
        this.currentUserSignal.set(user);
        localStorage.setItem('currentUser', JSON.stringify(user));
      })
    );
  }

  logout(): void {
    this.currentUserSignal.set(null);
    localStorage.removeItem('currentUser');
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

  hasRole(role: string): boolean {
    const user = this.currentUserSignal();
    if (!user) return false;
    return user.role === "ROLE_" + role.toUpperCase();
  }

  forgotPassword(email: string): Observable<string> {
    return this.http.post(`${environment.apiUrl}/auth/forgot-password`, { email }, { responseType: 'text' });
  }

  verifyCode(email: string, code: string): Observable<string> {
    return this.http.post(`${environment.apiUrl}/auth/verify-code`, null, {
      params: { email, code },
      responseType: 'text'
    });
  }

  resetPassword(data: any): Observable<string> {
    return this.http.post(`${environment.apiUrl}/auth/reset-password`, data, { responseType: 'text' });
  }

  changePassword(email: string, data: any): Observable<string> {
    return this.http.post(`${environment.apiUrl}/auth/change-password`, data, {
      params: { email },
      responseType: 'text'
    });
  }
}
