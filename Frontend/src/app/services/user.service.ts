import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/users`;

    getAllUsers(): Observable<User[]> {
        return this.http.get<User[]>(this.apiUrl);
    }

    getById(id: number): Observable<User> {
        return this.http.get<User>(`${this.apiUrl}/${id}`);
    }

    createUser(user: Partial<User>): Observable<User> {
        return this.http.post<User>(this.apiUrl, user);
    }

    updateUser(id: number, user: Partial<User>): Observable<User> {
        return this.http.put<User>(`${this.apiUrl}/${id}`, user);
    }

    deleteUser(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    getEnseignants(): Observable<User[]> {
        // Fetch all users and filter by role 'ROLE_ENSEIGNANT'
        // Ideally backend should provide /users/role/ROLE_ENSEIGNANT
        return new Observable(observer => {
            this.getAllUsers().subscribe({
                next: (users) => {
                    const enseignants = users.filter(u => u.role === 'ROLE_ENSEIGNANT');
                    observer.next(enseignants);
                    observer.complete();
                },
                error: (err) => observer.error(err)
            });
        });
    }
}
