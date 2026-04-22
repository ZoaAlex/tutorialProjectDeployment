# Gestion des Emplois du Temps - IUSJC

Application Angular pour la gestion des emplois du temps et réservations de salles de l'Institut Universitaire Saint Jean du Cameroun.

## Fonctionnalités

### Authentification
- Login/Register avec rôles (Admin, Enseignant, Étudiant)
- Comptes de test:
  - `admin@iusjc.cm` / `admin123`
  - `prof@iusjc.cm` / `prof123`
  - `etudiant@iusjc.cm` / `etud123`

### Emploi du temps
- Calendrier interactif (FullCalendar)
- Sélection de date unique ou plage de dates
- Filtrage par école (SJI, SJM, PrepaVogt, CPGE)
- Vue calendrier et vue liste
- Export PDF (à connecter au backend)

### Gestion des salles
- Liste des salles avec équipements
- Recherche de disponibilité par date/heure
- Filtrage par type et disponibilité

### Réservations
- Création de réservations
- Suivi des statuts (confirmée, en attente, annulée)

### Administration
- Gestion des utilisateurs (admin uniquement)
- Gestion des cours

## Installation

```bash
cd gestion-emploi-temps
npm install
ng serve
```

L'application sera disponible sur `http://localhost:4200`

## Technologies
- Angular 20
- Bootstrap 5
- FullCalendar
- FontAwesome
