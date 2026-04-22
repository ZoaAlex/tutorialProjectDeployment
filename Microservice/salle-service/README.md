# Salles-Service - Microservice de Gestion des Salles et du Matériel

## Description

Microservice dédié à la gestion des salles, du matériel et des réservations pour le système IUSJC Planning. Ce service fait partie de l'architecture microservices du projet de gestion des emplois du temps universitaires.

## Fonctionnalités

### Gestion des Salles
- CRUD complet des salles
- Recherche multicritères (capacité, type, équipements, disponibilité)
- Gestion des statuts (Libre, Occupée, Maintenance, Hors service)
- Vérification de disponibilité pour une période donnée
- Statistiques et reporting

### Gestion du Matériel
- CRUD du matériel par salle
- Gestion des états (Fonctionnel, En panne, En maintenance)
- Programmation et suivi de maintenance
- Déplacement de matériel entre salles
- Alertes de maintenance préventive

### Gestion des Réservations
- Création de réservations avec validation automatique des conflits
- Workflow de validation/rejet par les administrateurs
- Support des réservations récurrentes
- Annulation et modification de réservations
- Statistiques et historique

### Intégrations
- **Planning-Service** : Récupération des informations des écoles via Feign Client
- **RabbitMQ** : Notifications asynchrones pour tous les événements
- **Eureka** : Découverte de services
- **API Gateway** : Point d'entrée centralisé

## Technologies

- **Framework** : Spring Boot 3.2.4
- **Java** : 21
- **Base de données** : MySQL 8.0
- **ORM** : Spring Data JPA
- **Communication** : Spring Cloud OpenFeign
- **Messaging** : RabbitMQ (Spring AMQP)
- **Service Discovery** : Netflix Eureka Client
- **Cache** : Spring Cache
- **Build** : Maven

## Architecture

```
salles-service/
├── src/main/java/iusjc_planning/salles_service/
│   ├── config/          # Configurations (RabbitMQ, Cache)
│   ├── controller/      # Contrôleurs REST
│   ├── dto/            # Data Transfer Objects
│   ├── exception/      # Gestion des exceptions
│   ├── feign/          # Clients Feign
│   ├── mapper/         # Mappers entité ↔ DTO
│   ├── model/          # Entités JPA
│   ├── repository/     # Repositories Spring Data
│   └── service/        # Services métier
└── src/main/resources/
    └── application.properties
```

## Configuration

### Base de données
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sallesdb
spring.datasource.username=root
spring.datasource.password=root
```

### Services externes
```properties
services.planning-service.url=http://localhost:8083
services.user-service.url=http://localhost:8081
```

### RabbitMQ
```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

## Endpoints API

### Salles
- `GET /api/salles` - Liste toutes les salles
- `GET /api/salles/{id}` - Récupère une salle par ID
- `POST /api/salles` - Crée une nouvelle salle
- `PUT /api/salles/{id}` - Met à jour une salle
- `DELETE /api/salles/{id}` - Supprime une salle
- `GET /api/salles/disponibles` - Salles disponibles pour une période
- `GET /api/salles/ecole/{ecoleId}` - Salles d'une école
- `POST /api/salles/recherche` - Recherche multicritères

### Matériel
- `GET /api/materiels` - Liste tout le matériel
- `GET /api/materiels/{id}` - Récupère un matériel par ID
- `POST /api/materiels` - Crée un nouveau matériel
- `PUT /api/materiels/{id}` - Met à jour un matériel
- `DELETE /api/materiels/{id}` - Supprime un matériel
- `GET /api/materiels/salle/{salleId}` - Matériel d'une salle
- `GET /api/materiels/panne` - Matériel en panne
- `GET /api/materiels/maintenance/requise` - Matériel nécessitant maintenance

### Réservations
- `GET /api/reservations` - Liste toutes les réservations
- `GET /api/reservations/{id}` - Récupère une réservation par ID
- `POST /api/reservations` - Crée une nouvelle réservation
- `PUT /api/reservations/{id}` - Met à jour une réservation
- `DELETE /api/reservations/{id}` - Supprime une réservation
- `PATCH /api/reservations/{id}/valider` - Valide une réservation
- `PATCH /api/reservations/{id}/rejeter` - Rejette une réservation
- `PATCH /api/reservations/{id}/annuler` - Annule une réservation
- `GET /api/reservations/actives` - Réservations actives
- `GET /api/reservations/en-attente` - Réservations en attente

### Écoles (via Planning-Service)
- `GET /api/ecoles` - Liste toutes les écoles
- `GET /api/ecoles/{id}` - Récupère une école par ID
- `GET /api/ecoles/actives` - Écoles actives
- `GET /api/ecoles/universites` - Liste des universités

## Démarrage

### Prérequis
- Java 21
- Maven 3.9+
- MySQL 8.0
- RabbitMQ
- Eureka Server (port 8761)

### Lancement
```bash
cd salles-service
mvn clean install
mvn spring-boot:run
```

Le service démarre sur le port **8084**.

## Notifications RabbitMQ

Le service publie des événements sur RabbitMQ :
- `salle.created` - Salle créée
- `salle.updated` - Salle mise à jour
- `salle.deleted` - Salle supprimée
- `reservation.created` - Réservation créée
- `reservation.validated` - Réservation validée
- `reservation.rejected` - Réservation rejetée
- `materiel.maintenance` - Maintenance requise

## Modèle de données

### Salle
- Code salle (unique)
- Nom, capacité, type
- Statut (Libre, Occupée, Maintenance, Hors service)
- Localisation (bâtiment, étage, emplacement)
- Équipements (WiFi, climatisation, accessibilité handicap)
- École de rattachement

### Matériel
- Nom, type, quantité
- État (Fonctionnel, En panne, En maintenance)
- Marque, modèle, numéro de série
- Dates de maintenance
- Salle de rattachement

### Réservation
- Dates et heures (début, fin)
- Motif, description
- Statut (En attente, Validée, Rejetée, Annulée, Terminée)
- Utilisateur, nombre de participants
- Support des réservations récurrentes

## Auteurs

Équipe IUSJC Planning System

## Licence

Propriétaire - Institut Universitaire Saint-Jean Chrysostome 
