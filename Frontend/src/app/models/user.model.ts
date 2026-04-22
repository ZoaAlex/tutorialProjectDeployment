export interface User {
  id: number;
  email: string;
  password?: string;
  nom: string;
  prenom: string;
  role: string;
  ecole?: string;
  filiere?: string;
  niveau?: string;
  telephone?: string;
  avatar?: string;
  specialite?: string;
  grade?: string;
  statut?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  user: User;
  token: string;
}
