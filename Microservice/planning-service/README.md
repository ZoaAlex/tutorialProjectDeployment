# Planning Service - Université Saint-Jean Chrysostome (IUSJC)

## Vue d'ensemble

Service de gestion des plannings et des enseignants pour l'université Saint-Jean Chrysostome. Ce microservice gère les enseignants, leurs disponibilités, les cours, et la détection/résolution automatique des conflits de planning.

## Architecture

- **Framework** : Spring Boot 3.2.4
- **Base de données** : MySQL 8.0
- **Messaging** : RabbitMQ (notifications)
- **Découverte** : Eureka Client
- **API** : REST avec validation

## Demarrage Rapide

### Prérequis
- Java 21+
- Maven 3.9+
- MySQL 8.0
- RabbitMQ (optionnel pour notifications)
- Eureka Server (optionnel pour découverte de service)

### Installation
```bash
# Cloner et compiler
git clone <repository>
cd planning-service
mvn clean compile

# Initialiser la base de données MySQL
mysql -u root -p < donnees_base.sql

# Démarrer l'application
mvn spring-boot:run
```

### Configuration Base de Données
Modifier `src/main/resources/application.properties` selon votre environnement:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/planningdb?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=VOTRE_MOT_DE_PASSE
```

### Vérification
- Application : http://localhost:8082
- API Documentation : http://localhost:8082/api/enseignants
- Eureka Dashboard : http://localhost:8761 (si Eureka Server actif)

## API Endpoints

### Universites
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/universites` | Lister toutes les universités |
| POST | `/api/universites` | Créer une université |
| GET | `/api/universites/{id}` | Obtenir une université |
| PUT | `/api/universites/{id}` | Modifier une université |
| DELETE | `/api/universites/{id}` | Supprimer une université |

### Ecoles
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/ecoles` | Lister toutes les écoles |
| POST | `/api/ecoles` | Créer une école |
| GET | `/api/ecoles/{id}` | Obtenir une école |
| GET | `/api/ecoles/universite/{universiteId}` | Écoles d'une université |
| PUT | `/api/ecoles/{id}` | Modifier une école |
| DELETE | `/api/ecoles/{id}` | Supprimer une école |

### Enseignants
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/enseignants` | Lister tous les enseignants |
| POST | `/api/enseignants` | Créer un enseignant |
| GET | `/api/enseignants/{id}` | Obtenir un enseignant |
| PUT | `/api/enseignants/{id}` | Modifier un enseignant |
| DELETE | `/api/enseignants/{id}` | Supprimer un enseignant |
| GET | `/api/enseignants/search` | Rechercher des enseignants |
| GET | `/api/enseignants/{id}/stats` | Statistiques d'un enseignant |

### Disponibilites
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/disponibilites` | Lister toutes les disponibilités |
| POST | `/api/disponibilites` | Créer une disponibilité |
| GET | `/api/disponibilites/{id}` | Obtenir une disponibilité |
| GET | `/api/disponibilites/enseignant/{enseignantId}` | Disponibilités d'un enseignant |
| PUT | `/api/disponibilites/{id}` | Modifier une disponibilité |
| DELETE | `/api/disponibilites/{id}` | Supprimer une disponibilité |

### Cours
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/cours` | Lister tous les cours |
| POST | `/api/cours` | Créer un cours |
| GET | `/api/cours/{id}` | Obtenir un cours |
| GET | `/api/cours/enseignant/{enseignantId}` | Cours d'un enseignant |
| GET | `/api/cours/ecole/{ecoleId}` | Cours d'une école |
| PUT | `/api/cours/{id}` | Modifier un cours |
| PATCH | `/api/cours/{id}/statut` | Changer le statut d'un cours |
| DELETE | `/api/cours/{id}` | Supprimer un cours |

### Conflits
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/conflits` | Lister tous les conflits |
| GET | `/api/conflits/{id}` | Obtenir un conflit |
| GET | `/api/conflits/enseignant/{enseignantId}` | Conflits d'un enseignant |
| POST | `/api/conflits/{id}/resoudre` | Résoudre un conflit manuellement |
| GET | `/api/conflits/stats` | Statistiques des conflits |

### Resolution Automatique
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/resolution-auto/regles` | Lister les règles de résolution |
| POST | `/api/resolution-auto/regles` | Créer une règle |
| PUT | `/api/resolution-auto/regles/{id}` | Modifier une règle |
| POST | `/api/resolution-auto/executer` | Exécuter la résolution automatique |

## Exemples d'Utilisation

### Créer une Université
```json
POST /api/universites
{
  "nom": "Université Saint-Jean Chrysostome",
  "code": "IUSJC",
  "description": "Université privée spécialisée dans les sciences et technologies",
  "adresse": "123 Avenue Saint-Jean, Kinshasa",
  "telephone": "+243123456789",
  "email": "info@iusjc.edu",
  "statut": "ACTIVE"
}
```

### Créer une École
```json
POST /api/ecoles
{
  "nom": "École Supérieure d'Informatique et de Gestion",
  "code": "ESIG",
  "description": "Formation en informatique et gestion",
  "statut": "ACTIVE",
  "universiteId": 1
}
```

### Créer un Enseignant
```json
POST /api/enseignants
{
  "nom": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@iusjc.edu",
  "specialite": "Mathématiques",
  "grade": "Professeur",
  "departement": "Sciences Exactes",
  "statut": "ACTIF"
}
```

### Créer une Disponibilité
```json
POST /api/disponibilites
{
  "enseignantId": 1,
  "jour": "LUNDI",
  "heureDebut": "08:00:00",
  "heureFin": "12:00:00",
  "type": "MATIN",
  "estDisponible": true
}
```

### Créer un Cours
```json
POST /api/cours
{
  "nomCours": "Mathématiques I",
  "codeCours": "MATH101",
  "jour": "LUNDI",
  "heureDebut": "08:00:00",
  "heureFin": "10:00:00",
  "salle": "A101",
  "typeCours": "COURS_MAGISTRAL",
  "ecoleId": 1,
  "enseignantId": 1
}
```

## Configuration

### Base de Données
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/planningdb
spring.datasource.username=root
spring.datasource.password=dell
```

### RabbitMQ (Optionnel)
```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

### Eureka (Découverte de Service)
```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
```

## Fonctionnalites Avancees

### Détection Automatique de Conflits
Le système détecte automatiquement 4 types de conflits :
- **Chevauchement horaire** entre écoles
- **Indisponibilité enseignant**
- **Conflit de salle**
- **Surcharge horaire**

### Résolution Automatique
8 actions de résolution automatique :
- Décaler cours suivant/précédent
- Changer de salle
- Réduire durée
- Suspendre cours
- Notifier administrateur
- Proposer alternatives

### Notifications RabbitMQ
Messages automatiques vers le service de notifications :
- Conflit détecté
- Cours suspendu
- Résolution automatique
- Surcharge enseignant

## Donnees d'Exemple

Le service inclut des données d'exemple :
- Université Saint-Jean Chrysostome
- 4 écoles (ESIG, ESGC, MÉDECINE, INGÉNIERIE)
- 3 enseignants avec spécialités
- Règles de résolution automatique
- Disponibilités d'exemple

### Initialisation
```bash
mysql -u root -p < donnees_base.sql
```

## Tests

### Collection Postman
Utilisez `Planning-Service-API.postman_collection.json` :
1. Importer dans Postman
2. Définir `baseUrl` = `http://localhost:8082`
3. Tester tous les endpoints

### Tests Manuels
```bash
# Vérifier les enseignants
curl http://localhost:8082/api/enseignants

# Vérifier les écoles
curl http://localhost:8082/api/ecoles

# Créer un cours et observer la détection de conflits
curl -X POST http://localhost:8082/api/cours \
  -H "Content-Type: application/json" \
  -d '{"nomCours":"Test","jour":"LUNDI","heureDebut":"08:00:00","heureFin":"10:00:00","typeCours":"COURS_MAGISTRAL","ecoleId":1,"enseignantId":1}'
```

## Monitoring

### Logs
```bash
# Logs de l'application
tail -f logs/planning-service.log

# Logs de détection de conflits
grep "Conflit détecté" logs/planning-service.log
```

### Métriques
- Nombre de conflits détectés
- Taux de résolution automatique
- Temps de réponse API
- Utilisation base de données

## Deploiement

### Environnement de Production
1. Configurer MySQL avec utilisateur dédié
2. Configurer RabbitMQ avec vhost dédié
3. Démarrer Eureka Server
4. Déployer l'application
5. Vérifier l'enregistrement dans Eureka

### Docker (Optionnel)
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/planning-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Integration Microservices

Ce service s'intègre avec :
- **User Service** : Gestion des utilisateurs
- **Notification Service** : Envoi de notifications
- **Auth Service** : Authentification
- **API Gateway** : Routage des requêtes

### Communication
- **Synchrone** : API REST via Feign Client
- **Asynchrone** : Messages RabbitMQ
- **Découverte** : Eureka Client

## Support

Pour toute question ou problème :
1. Vérifier les logs de l'application
2. Consulter la documentation des endpoints
3. Tester avec la collection Postman
4. Vérifier la configuration des services externes

## Evolutions Futures

- Interface web d'administration
- Notifications push en temps réel
- Intégration calendrier externe
- Rapports et analytics avancés
- API GraphQL

## Depannage

### Problèmes Courants

#### Erreur de Compilation
```bash
# Nettoyer et recompiler
mvn clean compile

# Vérifier la version Java
java -version
# Doit être Java 21+
```

#### Erreur de Base de Données
```bash
# Vérifier MySQL
mysql -u root -p -e "SHOW DATABASES;"

# Recréer la base
mysql -u root -p -e "DROP DATABASE IF EXISTS planningdb; CREATE DATABASE planningdb;"
mysql -u root -p planningdb < donnees_base.sql
```

#### Erreur 404 sur les Endpoints
```bash
# Vérifier que les données de base sont présentes
curl http://localhost:8082/api/universites
curl http://localhost:8082/api/ecoles

# Si vide, réinitialiser les données
mysql -u root -p planningdb < donnees_base.sql
```

#### Service ne Démarre Pas
1. Vérifier le port 8082 n'est pas utilisé
2. Vérifier MySQL est démarré
3. Vérifier les logs : `tail -f logs/planning-service.log`
4. Utiliser le script de vérification : `verify-startup.bat`

#### Conflits Git
```bash
# Si des conflits persistent
git status
git reset --hard HEAD
git clean -fd
```

### Script de Vérification
Utilisez `verify-startup.bat` pour un diagnostic complet :
```bash
./verify-startup.bat
```

### Logs Utiles
```bash
# Erreurs de compilation
mvn clean compile

# Erreurs de démarrage
mvn spring-boot:run

# Logs applicatifs
tail -f logs/planning-service.log
```